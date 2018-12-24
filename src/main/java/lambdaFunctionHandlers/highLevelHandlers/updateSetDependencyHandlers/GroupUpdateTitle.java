package main.java.lambdaFunctionHandlers.highLevelHandlers.updateSetDependencyHandlers;

import main.java.Logic.Constants;
import main.java.databaseObjects.Group;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.GroupDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

public class GroupUpdateTitle {
    public static List<DatabaseAction> getActions(String fromID, String groupID, String title) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        if (!(Group.readGroup(groupID).owners.contains(fromID)) && !fromID.equals(Constants.adminKey)) {
            throw new Exception("PERMISSIONS ERROR: You can only update a group that you own!");
        }

        // Get all the actions for this process
        databaseActions.add(GroupDatabaseActionBuilder.updateTitle(groupID, title));

        return databaseActions;
    }
}
