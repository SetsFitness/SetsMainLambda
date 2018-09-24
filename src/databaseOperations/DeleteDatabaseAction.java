package databaseOperations;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

public class DeleteDatabaseAction extends DatabaseAction {
    public DeleteDatabaseAction(Map<String, AttributeValue> key) {
        this.action = DBAction.DELETE;
        this.item = key;

        // Unused variables
        this.checkHandler = null;
    }

//    public DeleteDatabaseAction(Map<String, AttributeValue> key, String conditionalExpression, CheckHandler checkHandler) {
//        this.action = DBAction.DELETESAFE;
//        this.item = key;
//        this.conditionalExpression = conditionalExpression;
//        this.checkHandler = checkHandler;
//
//        // Unused variables
//        this.updateItem = null;
//    }
}
