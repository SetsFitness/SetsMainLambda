package main.java.lambdaFunctionHandlers.highLevelHandlers.createDependencyHandlers;

import main.java.logic.Constants;
import main.java.logic.ItemType;
import main.java.databaseObjects.User;
import main.java.databaseOperations.DatabaseActionCompiler;
import main.java.databaseOperations.databaseActionBuilders.ReviewDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.UserDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.WorkoutDatabaseActionBuilder;
import main.java.lambdaFunctionHandlers.requestObjects.CreateReviewRequest;

import java.util.*;

/**
 * Creates a Review in the database, checks the inputs, adds the Review to the sender's authored
 * reviews and the receiver's reviews, and updates the receiver's current rating.
 */
public class CreateReview {
    public static List<DatabaseActionCompiler> getCompilers(String fromID, CreateReviewRequest createReviewRequest, String surveyWorkoutID, int depth) throws Exception {
        if (createReviewRequest != null) {
            if (createReviewRequest.by != null && createReviewRequest.about != null && createReviewRequest
                    .friendlinessRating != null && createReviewRequest.effectivenessRating != null &&
                    createReviewRequest.reliabilityRating != null && createReviewRequest.description != null) {
                List<DatabaseActionCompiler> compilers = new ArrayList<>();
                DatabaseActionCompiler databaseActionCompiler = new DatabaseActionCompiler();

                if (fromID == null || (!fromID.equals(createReviewRequest.by) && !Constants.isAdmin(fromID))) {
                    throw new Exception("PERMISSIONS ERROR: You can only create reviews you have authored!");
                }

                // Create Review
                // TODO If we ever try to create Reviews automatically, figure out which
                // TODO attributes need which passover Identifiers.
                databaseActionCompiler.add(ReviewDatabaseActionBuilder.create(createReviewRequest, null));

                // Add to by's reviews by
                String byID = createReviewRequest.by;
                String byItemType = ItemType.getItemType(byID);
                if (byItemType == null) {
                    throw new Exception("Review By is invalid!");
                }
                databaseActionCompiler.add(UserDatabaseActionBuilder.updateAddReviewBy(createReviewRequest.by,
                        byItemType, null, true));

                // Add to about's reviews about
                String about = createReviewRequest.about;
                String aboutItemType = ItemType.getItemType(about);
                if (aboutItemType == null) {
                    throw new Exception("Review AboutID is invalid!");
                }
                databaseActionCompiler.add(UserDatabaseActionBuilder.updateAddReviewAbout(about, aboutItemType,
                        null, true));

                // Calculate about's ratings
                User user = User.readUser(about, aboutItemType);
                float friendlinessRating = Float.parseFloat(createReviewRequest.friendlinessRating);
                float effectivenessRating = Float.parseFloat(createReviewRequest.effectivenessRating);
                float reliabilityRating = Float.parseFloat(createReviewRequest.reliabilityRating);
                Set<String> reviewsAbout = user.reviewsAbout;
                int numReviews = reviewsAbout.size();

                // Basically it finds the "sum" of the ratings, using the current rating and the number of
                // reviews. Then, it adds our rating value to it, then divides it by numReviews + 1.
                float newFriendlinessRating = ((numReviews * user.friendlinessRating) +
                        friendlinessRating) / (numReviews + 1);
                float newEffectivenessRating = ((numReviews * user.effectivenessRating) +
                        effectivenessRating) / (numReviews + 1);
                float newReliabilityRating = ((numReviews * user.reliabilityRating) +
                        reliabilityRating) / (numReviews + 1);

                // Updates the about item
                databaseActionCompiler.add(UserDatabaseActionBuilder.updateFriendlinessRating(about,
                        aboutItemType, Float.toString(newFriendlinessRating)));
                databaseActionCompiler.add(UserDatabaseActionBuilder.updateEffectivenessRating(about,
                        aboutItemType, Float.toString(newEffectivenessRating)));
                databaseActionCompiler.add(UserDatabaseActionBuilder.updateReliabilityRating(about,
                        aboutItemType, Float.toString(newReliabilityRating)));

                if (surveyWorkoutID != null) {
                    // TODO THIS SHOULD ALSO MOVE THE WORKOUT FROM SCHEDULED TO COMPLETED?
                    databaseActionCompiler.add(WorkoutDatabaseActionBuilder.updateRemoveMissingReview(surveyWorkoutID, byID,
                            true));
                }

                compilers.add(databaseActionCompiler);

                return compilers;
            }
            else {
                throw new Exception("createReviewRequest is missing required fields!");
            }
        }
        else {
            throw new Exception("createReviewRequest not initialized for CREATE statement!");
        }
    }
}
