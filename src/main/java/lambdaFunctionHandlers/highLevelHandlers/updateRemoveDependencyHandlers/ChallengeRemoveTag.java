package main.java.lambdaFunctionHandlers.highLevelHandlers.updateRemoveDependencyHandlers;

import main.java.databaseOperations.exceptions.PermissionsException;
import main.java.logic.Constants;
import main.java.databaseObjects.Challenge;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.ChallengeDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes a String tag from a Challenge.
 */
public class ChallengeRemoveTag {
    public static List<DatabaseAction> getActions(String fromID, String challengeID, String tag) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        Challenge challenge = Challenge.readChallenge(challengeID);

        if (fromID == null || (!fromID.equals(challenge.owner) && !Constants.isAdmin(fromID))) {
            throw new PermissionsException("You can only update a gym you own!");
        }

        databaseActions.add(ChallengeDatabaseActionBuilder.updateRemoveTag(challengeID, tag));

        return databaseActions;
    }
}
