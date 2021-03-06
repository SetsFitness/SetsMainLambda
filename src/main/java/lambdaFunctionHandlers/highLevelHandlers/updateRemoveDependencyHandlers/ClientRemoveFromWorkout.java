package main.java.lambdaFunctionHandlers.highLevelHandlers.updateRemoveDependencyHandlers;

import main.java.databaseOperations.exceptions.PermissionsException;
import main.java.logic.Constants;
import main.java.databaseObjects.Client;
import main.java.databaseObjects.Workout;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.UserDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.WorkoutDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes a Client from a Workout, deleting the Workout if it is empty. TODO Revisit
 */
public class ClientRemoveFromWorkout {
    public static List<DatabaseAction> getActions(String fromID, String clientID, String workoutID) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        Workout workout = Workout.readWorkout(workoutID);
        Client client = Client.readClient(clientID);

        if (fromID == null || (!fromID.equals(clientID) && !Constants.isAdmin(fromID))) {
            throw new PermissionsException("You can only remove yourself from a workout!");
        }

        // We delete the workout from ourselves
        databaseActions.add(UserDatabaseActionBuilder.updateRemoveScheduledWorkout(clientID, "Client", workoutID));
        databaseActions.add(UserDatabaseActionBuilder.updateRemoveScheduledTime(clientID, "Client", workout.time
                .toString()));

        // And we delete ourselves from the workout
        databaseActions.add(WorkoutDatabaseActionBuilder.updateRemoveClient(workoutID, clientID));
        databaseActions.add(WorkoutDatabaseActionBuilder.updateRemoveMissingReview(workoutID, clientID, false));

        // Then we delete the workout only if it is empty
        databaseActions.add(WorkoutDatabaseActionBuilder.deleteIfEmpty(workoutID));

        // If this was a scheduled workout, we would need to be refunded for it
        if (client.scheduledWorkouts.contains(workoutID)) {
            // TODO THIS IS WHERE REFUNDS WOULD GO (As a process that goes after the successful completion?)
            // TODO Once we actually do Stripe lmao
        }

        return databaseActions;
    }
}
