package main.java.lambdaFunctionHandlers.highLevelHandlers.updateAddDependencyHandlers;

import main.java.databaseObjects.Workout;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.ClientDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.WorkoutDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

public class ClientAddToWorkout {
    public static List<DatabaseAction> getActions(String clientID, String workoutID) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        // Get all the actions for this process
        Workout workout = Workout.readWorkout(workoutID);
        // Add to client's scheduled workouts
        databaseActions.add(ClientDatabaseActionBuilder.updateAddScheduledWorkout(clientID,
                workoutID, false));
        // Add to client's scheduled workout times
        databaseActions.add(ClientDatabaseActionBuilder.updateAddScheduledTime(clientID, workout.time.toString
                ()));
        // Add to workout's clients
        databaseActions.add(WorkoutDatabaseActionBuilder.updateAddClientID(workoutID, clientID));
        // Add to workout's missingReviews
        databaseActions.add(WorkoutDatabaseActionBuilder.updateAddMissingReview
                (workoutID, clientID));

        return databaseActions;
    }
}
