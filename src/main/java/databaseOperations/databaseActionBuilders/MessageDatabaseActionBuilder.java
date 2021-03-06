package main.java.databaseOperations.databaseActionBuilders;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import main.java.databaseObjects.Message;
import main.java.databaseOperations.CreateDatabaseAction;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.DeleteDatabaseAction;
import main.java.databaseOperations.UpdateDatabaseAction;
import main.java.databaseOperations.UpdateWithIDHandler;
import main.java.lambdaFunctionHandlers.requestObjects.CreateMessageRequest;

import java.util.HashMap;
import java.util.Map;

import static main.java.databaseOperations.UpdateDatabaseAction.UpdateAction.*;

/**
 * The Database Action Builder for the {@link Message} object, getting the {@link DatabaseAction} objects
 * that dictate the individual actions to do in the database for Messages.
 */
public class MessageDatabaseActionBuilder {
    private static final String itemType = "Message";

    /**
     * Gets the {@link PrimaryKey} object to identify the message in the database.
     *
     * @param board The name of the board containing the message in the database.
     * @param id The ID of the message to reference.
     * @return The {@link PrimaryKey} object to identify the database item with.
     */
    private static PrimaryKey getPrimaryKey(String board, String id) {
        return new PrimaryKey("board", board, "id", id);
    }

    public static DatabaseAction create(CreateMessageRequest createMessageRequest, Map<String, String> passoverIdentifiers) {
        // Handle the setting of the items
        Map<String, AttributeValue> item = Message.getEmptyItem();
        item.put("board", new AttributeValue(createMessageRequest.board));
        item.put("from", new AttributeValue(createMessageRequest.from));
        item.put("message", new AttributeValue(createMessageRequest.message));
        item.put("name", new AttributeValue(createMessageRequest.name));
        if (createMessageRequest.type != null) { item.put("type", new AttributeValue(createMessageRequest.type)); }
        if (createMessageRequest.profileImagePath != null) {
            item.put("profileImagePath", new AttributeValue(createMessageRequest.profileImagePath));
        }
        return new CreateDatabaseAction(itemType, item, passoverIdentifiers,
            (Map<String, AttributeValue> createdItem, String id) -> {
                if (createMessageRequest.type != null) {
                    if (createMessageRequest.type.equals("picture") || createMessageRequest.type.equals("video")) {
                        createdItem.put("message", new AttributeValue(id + "/" + createMessageRequest.message));
                    }
                }
            }
        );
    }

    public static DatabaseAction updateAddLastSeenFor(String board, String id, String lastSeenFor) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(board, id), "lastSeenFor", new AttributeValue(lastSeenFor), false, ADD);
    }

    public static DatabaseAction delete(String board, String id) {
        return new DeleteDatabaseAction(id, itemType, getPrimaryKey(board, id));
    }
}
