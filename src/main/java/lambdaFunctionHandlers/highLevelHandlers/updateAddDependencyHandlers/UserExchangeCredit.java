package main.java.lambdaFunctionHandlers.highLevelHandlers.updateAddDependencyHandlers;

import java.util.ArrayList;
import java.util.List;

import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.UserDatabaseActionBuilder;
import main.java.databaseOperations.exceptions.PermissionsException;
import main.java.logic.Constants;

/**
 * Exchanges credits between Users. Fails if credit will fall below zero.
 */
public class UserExchangeCredit {
    public static List<DatabaseAction> getActions(String fromID, String fromUserID, String fromItemType,
                                                  String toUserID, String toItemType, int creditAmount) throws
            Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        if (fromID == null || (!fromID.equals(fromUserID) && !fromID.equals(toUserID) && !Constants.isAdmin(fromID))) {
            throw new PermissionsException("You can only send/receive credit as yourself!");
        }

        databaseActions.add(UserDatabaseActionBuilder.updateRemoveCredit(fromUserID, fromItemType, creditAmount));
        databaseActions.add(UserDatabaseActionBuilder.updateAddCredit(toUserID, toItemType, creditAmount));

        return databaseActions;
    }
}
