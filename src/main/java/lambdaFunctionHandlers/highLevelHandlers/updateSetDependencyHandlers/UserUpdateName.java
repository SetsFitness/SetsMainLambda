package main.java.lambdaFunctionHandlers.highLevelHandlers.updateSetDependencyHandlers;

import main.java.Logic.Constants;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.UserDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateName {
    public static List<DatabaseAction> getActions(String fromID, String userID, String itemType, String name) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        if (!fromID.equals(userID) && !fromID.equals(Constants.adminKey)) {
            throw new Exception("PERMISSIONS ERROR: You can only update a user you are!");
        }
        // Get all the actions for this process
        databaseActions.add(UserDatabaseActionBuilder.updateName(userID, itemType, name));

        return databaseActions;
    }
}
