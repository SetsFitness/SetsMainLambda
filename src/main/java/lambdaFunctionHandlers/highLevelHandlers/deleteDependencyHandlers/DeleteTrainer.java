package main.java.lambdaFunctionHandlers.highLevelHandlers.deleteDependencyHandlers;

import main.java.Logic.Constants;
import main.java.databaseObjects.Trainer;
import main.java.databaseOperations.DatabaseAction;
import main.java.databaseOperations.databaseActionBuilders.ClientDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.GymDatabaseActionBuilder;
import main.java.databaseOperations.databaseActionBuilders.TrainerDatabaseActionBuilder;

import java.util.ArrayList;
import java.util.List;

public class DeleteTrainer {
    public static List<DatabaseAction> getActions(String fromID, String trainerID) throws Exception {
        List<DatabaseAction> databaseActions = new ArrayList<>();

        Trainer trainer = Trainer.readTrainer(trainerID);

        if (!fromID.equals(trainerID) && !fromID.equals(trainer.gym) && !fromID.equals(Constants.adminKey)) {
            throw new Exception("PERMISSIONS ERROR: You can only delete a trainer if it's yourself or your gym!");
        }

        // TODO =======================================================================================================
        // TODO We should be deleting far fewer "dependencies" in order to make sure as little info as possible is lost
        // TODO =======================================================================================================

        // Delete the user associated with the trainer
        databaseActions.addAll(DeleteUser.getActions(fromID, trainer));

        // Remove all workouts in scheduled workouts and completed workouts (Cancel them)
        for (String workoutID : trainer.scheduledWorkouts) {
            databaseActions.addAll(DeleteWorkout.getActions(trainerID, workoutID));
        }
//        for (String workoutID : trainer.completedWorkouts) {
//            databaseActions.addAll(DeleteWorkout.getActions(trainerID, workoutID));
//        }

        for (String subscriberID : trainer.subscribers) {
            databaseActions.add(ClientDatabaseActionBuilder.updateRemoveSubscription(trainerID, subscriberID));
        }

        // Remove from gym's trainers field
        databaseActions.add(GymDatabaseActionBuilder.updateRemoveTrainer(trainer.gym, trainerID));

        // Delete the Trainer
        databaseActions.add(TrainerDatabaseActionBuilder.delete(trainerID));

        return databaseActions;
    }
}
