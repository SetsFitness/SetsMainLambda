package main.java.lambdaFunctionHandlers.highLevelHandlers.updateAddDependencyHandlers;

import java.util.ArrayList;
import java.util.List;

import main.java.Logic.Constants;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.PostDatabaseActionBuilder;

public class PostAddLike {
    public static List<DatabaseAction> getActions(String fromID, String postID, String like) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        if (!fromID.equals(like) && !fromID.equals(Constants.adminKey)) {
            throw new Exception("PERMISSIONS ERROR: You can only like as yourself!");
        }

        databaseActions.add(PostDatabaseActionBuilder.updateAddLike(postID, like));

        return databaseActions;
    }
}
