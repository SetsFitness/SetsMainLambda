package main.java.databaseOperations;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import main.java.logic.Constants;
import main.java.databaseObjects.DatabaseItem;
import main.java.notifications.NotificationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class takes in database actions and compiles them together so that in a single transaction,
 * there are no duplicate actions to an item. Also increases the efficiency of the transaction.
 */
public class DatabaseActionCompiler {
    // TODO JAVADOC ALL METHODS
    private String passoverIdentifier;
    private NotificationHandler notificationHandler;
    private List<DatabaseAction> databaseActions;
    private Map<String, DatabaseAction> databaseActionMap;

    public DatabaseActionCompiler() throws Exception {
        databaseActions = new ArrayList<>();
        databaseActionMap = new HashMap<>();
        notificationHandler = new NotificationHandler();
    }

    public List<DatabaseAction> getDatabaseActions() {
        return databaseActions;
    }

    public void sendNotifications() throws Exception {
        notificationHandler.sendMessages();
    }

    public void add(DatabaseAction databaseAction) throws Exception {
        String id;
        if (databaseAction.action != DBAction.CREATE) {
            if (databaseAction.id.equals("")) {
                id = databaseAction.idIdentifier;
            }
            else {
                id = databaseAction.id;
            }
        }
        else {
            id = "create";
        }

        if (databaseActionMap.containsKey(id)) {
            // Combine the two database actions
            DatabaseAction existingDatabaseAction = databaseActionMap.get(id);
            DBAction existingAction = existingDatabaseAction.action;
            DBAction action = databaseAction.action;
            if (action == DBAction.CREATE) {
                if (existingAction == DBAction.CREATE) {
                    throw new Exception("INTERNAL ERROR: Can only have one CREATE statement for the object");
                }
                else {
                    throw new Exception("INTERNAL ERROR: Create must be first in the list!");
                }
            }
            else if (action == DBAction.DELETE || action == DBAction.DELETECONDITIONAL || existingAction == DBAction
                    .DELETE || existingAction == DBAction.DELETECONDITIONAL) {
                if (existingAction == DBAction.DELETE || existingAction == DBAction.DELETECONDITIONAL) {
                    if (action == DBAction.DELETE || action == DBAction.DELETECONDITIONAL) {
                        // TODO This might not actually be an error. If the deletion process gets more intense with
                        // TODO checks, then we might want to try to consolidate the two DELETECONDITIONAL statements
                        // Both are delete, this is an error
                        throw new Exception("INTERNAL ERROR: Can only DELETE an object once in a transaction.");
                    }
                    // Otherwise, the existing action is DELETE, so just forget about the changes
                    Constants.debugLog("Overwriting potential changes to an item by deleting it!");
                }
                else {
                    // Only action is DELETE
                    // This is fine, because any update will be usurped anyways, so just console it.
                    Constants.debugLog("Overwriting potential changes to an item by deleting it!");
                    existingDatabaseAction.action = databaseAction.action;
                    existingDatabaseAction.checkHandler = databaseAction.checkHandler;
                    existingDatabaseAction.ifWithCreate = false;
                }
            }
            else {
                consolidateDatabaseActions(existingDatabaseAction, databaseAction);
            }
        }
        else {
            // Then all is well, we can just do it
            databaseActions.add(databaseAction);
            databaseActionMap.put(id, databaseAction);
        }
    }

    private void consolidateDatabaseActions(DatabaseAction intoDatabaseAction, DatabaseAction fromDatabaseAction)
            throws Exception {
        DBAction intoAction = intoDatabaseAction.action;
        DBAction fromAction = fromDatabaseAction.action;

        // For DatabaseAction, update all of these:

//        Map<String, AttributeValue> item;
//        DBAction action;                                  DONe
//        Map<String, AttributeValueUpdate> updateItem;
//        CheckHandler checkHandler;                        DoNe
//        boolean ifWithCreate;                             DonE

        if (fromDatabaseAction.ifWithCreate) {
            intoDatabaseAction.ifWithCreate = true;
        }

        if (intoAction == DBAction.CREATE && (fromAction == DBAction.UPDATE || fromAction == DBAction.UPDATESAFE)) {
            // If the existing one is CREATE and other is UPDATE, then switch variables for the item (result is
            // CREATE) (If it's UPDATESAFE, same thing.)
            updateCreateItem(intoDatabaseAction.item, fromDatabaseAction.updateItem);
        }
        else if (intoAction == DBAction.UPDATE && fromAction == DBAction.UPDATE) {
            // If both are UPDATE, then add the updateItems together. (result is UPDATE)
            updateUpdateItem(intoDatabaseAction.updateItem, fromDatabaseAction.updateItem);
        }
        else if (intoAction == DBAction.UPDATE && fromAction == DBAction.UPDATESAFE) {
            // If the existing one is UPDATE and other is UPDATESAFE, set the checkHandler to one from UPDATESAFE
            // and add the updateItems together.) (result is UPDATESAFE) (same vice versa).
            updateUpdateItem(intoDatabaseAction.updateItem, fromDatabaseAction.updateItem);
            intoDatabaseAction.checkHandler = fromDatabaseAction.checkHandler;
            intoDatabaseAction.action = DBAction.UPDATESAFE;
        }
        else if (intoAction == DBAction.UPDATESAFE && fromAction == DBAction.UPDATE) {
            // If the existing one is UPDATE and other is UPDATESAFE, set the checkHandler to one from UPDATESAFE
            // and add the updateItems together.) (result is UPDATESAFE) (same vice versa).
            updateUpdateItem(intoDatabaseAction.updateItem, fromDatabaseAction.updateItem);
        }
        else if (intoAction == DBAction.UPDATESAFE && fromAction == DBAction.UPDATESAFE) {
            // If both are UPDATESAFE, then add the updateItems together and "&&" the two CheckHandlers together
            updateUpdateItem(intoDatabaseAction.updateItem, fromDatabaseAction.updateItem);
            intoDatabaseAction.checkHandler = new CheckHandler() {
                @Override
                public String isViable(DatabaseItem newItem) throws Exception {
                    String isViable1 = intoDatabaseAction.checkHandler.isViable(newItem);
                    String isViable2 = fromDatabaseAction.checkHandler.isViable(newItem);
                    if (isViable1 != null && isViable2 != null) {
                        return isViable1 + " AND " + isViable2;
                    }
                    else if (isViable1 != null) {
                        return isViable1;
                    }
                    else if (isViable2 != null) {
                        return isViable2;
                    }
                    else {
                        return null;
                    }
                }
            };
        }
        else {
            throw new Exception("INTERNAL ERROR: UNHANDLED COMBINATION OF ACTIONS");
        }
    }

    // This will use the updateItem from the databaseAction to adjust the item that will be created
    private void updateCreateItem(Map<String, AttributeValue> intoItem, Map<String, AttributeValueUpdate>
            fromUpdateItem) {
        for (Map.Entry<String, AttributeValueUpdate> entry: fromUpdateItem.entrySet()) {
            String attributeName = entry.getKey();
            AttributeValueUpdate attributeValueUpdate = entry.getValue();
            AttributeValue attributeValue = attributeValueUpdate.getValue();
            String action = attributeValueUpdate.getAction();

            if (action.equals("PUT")) {
                // Just set it
                intoItem.put(attributeName, attributeValue);
            }
            else if (action.equals("ADD")) {
                // Add it
                AttributeValue intoAttributeValue = intoItem.get(attributeName);
                if (intoAttributeValue == null) {
                    intoItem.put(attributeName, attributeValue);
                }
                else {
                    List<String> attributeStringList = intoAttributeValue.getSS();
                    attributeStringList.addAll(attributeValue.getSS());
                    AttributeValue newAttributeValue = new AttributeValue(attributeStringList);
                    intoItem.put(attributeName, newAttributeValue);
                }
            }
            else if (action.equals("DELETE")) {
                // Remove it
                AttributeValue intoAttributeValue = intoItem.get(attributeName);
                if (intoAttributeValue != null) {
                    List<String> attributeStringList = intoAttributeValue.getSS();
                    attributeStringList.removeAll(attributeValue.getSS());
                    AttributeValue newAttributeValue = new AttributeValue(attributeStringList);
                    intoItem.put(attributeName, newAttributeValue);
                }
            }
        }
    }

    // This will adjust the updateItem of the databaseAction to reflect both databaseActions
    private void updateUpdateItem(Map<String, AttributeValueUpdate> intoUpdateItem, Map<String, AttributeValueUpdate>
            fromUpdateItem) throws Exception {
        for (Map.Entry<String, AttributeValueUpdate> entry: fromUpdateItem.entrySet()) {
            String attributeName = entry.getKey();
            AttributeValueUpdate attributeValueUpdate = entry.getValue();
            // AttributeValue attributeValue = attributeValueUpdate.getValue();
            String action = attributeValueUpdate.getAction();

            if (intoUpdateItem.containsKey(attributeName)) {
                if (action.equals("PUT")) {
                    throw new Exception("INTERNAL ERROR: attempting to overwrite a SET update in a mulit-faceted " +
                            "update. Tried to update = " + attributeName);
                }
                else if (action.equals("ADD")) {
                    throw new Exception("INTERNAL ERROR: attempting to overwrite an ADD update in a multi-facted " +
                            "update. Tried to update = " + attributeName);
                }
                else if (action.equals("DELETE")) {
                    throw new Exception("INTERNAL ERROR: attempting to overwrite a REMOVE update in a multi-facted " +
                            "update. Tried to update = " + attributeName);
                }
            }
            else {
                // You can just add the statement, no overwriting
                intoUpdateItem.put(attributeName, attributeValueUpdate);
            }
        }
    }

    public void addAll(List<DatabaseAction> databaseActions) throws Exception {
        for (DatabaseAction databaseAction: databaseActions) {
            add(databaseAction);
        }
    }

    public NotificationHandler getNotificationHandler() {
        return this.notificationHandler;
    }

    public void setPassoverIdentifier(String passoverIdentifier) {
        this.passoverIdentifier = passoverIdentifier;
    }

    public String getPassoverIdentifier() {
        return this.passoverIdentifier;
    }

//    public void addMessage(String to, String type, JsonObjectBuilder payload) throws Exception {
////        notificationHandler.addMessage(to, type, payload);
//    }
}
