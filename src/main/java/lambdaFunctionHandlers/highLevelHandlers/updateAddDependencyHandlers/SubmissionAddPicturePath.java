package main.java.lambdaFunctionHandlers.highLevelHandlers.updateAddDependencyHandlers;

import java.util.ArrayList;
import java.util.List;

import main.java.databaseOperations.exceptions.PermissionsException;
import main.java.logic.Constants;
import main.java.databaseObjects.Submission;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.SubmissionDatabaseActionBuilder;

/**
 * Adds a picture path to an existing Submission, making sure that there aren't already too many.
 */
public class SubmissionAddPicturePath {
    public static List<DatabaseAction> getActions(String fromID, String submissionID, String picturePath) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        Submission submission = Submission.readSubmission(submissionID);

        if (fromID == null || (!fromID.equals(submission.by) && !Constants.isAdmin(fromID))) {
            throw new PermissionsException("You can only update a submission you own!");
        }

        if (submission.picturePaths != null && submission.picturePaths.size() >= Constants.postPicturePathsLimit) {
            throw new Exception("This post is already filled with the max number of pictures!");
        }

        // Get all the actions for this process
        databaseActions.add(SubmissionDatabaseActionBuilder.updateAddPicturePath(submissionID, picturePath));

        return databaseActions;
    }
}
