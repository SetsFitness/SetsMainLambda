package main.java.databaseOperations.databaseActionBuilders;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import main.java.databaseObjects.DatabaseItem;
import main.java.databaseObjects.DatabaseObject;
import main.java.databaseObjects.Gym;
import main.java.databaseObjects.TimeInterval;
import main.java.databaseOperations.*;
import main.java.lambdaFunctionHandlers.requestObjects.CreateGymRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.java.databaseOperations.UpdateDatabaseAction.UpdateAction.*;

/**
 * The Database Action Builder for the {@link Gym} object, getting the {@link DatabaseAction} objects
 * that dictate the individual actions to do in the database for Gyms.
 */
public class GymDatabaseActionBuilder {
    final static private String itemType = "Gym";

    /**
     * Gets the {@link PrimaryKey} object to identify the object in the database.
     *
     * @param id The ID of the object to reference.
     * @return The {@link PrimaryKey} object to identify the database item with.
     */
    private static PrimaryKey getPrimaryKey(String id) {
        return new PrimaryKey("item_type", itemType, "id", id);
    }

    public static DatabaseAction create(CreateGymRequest createGymRequest, Map<String, String> passoverIdentifiers) {
        // Handle the setting of the items!
        Map<String, AttributeValue> item = Gym.getEmptyItem();
        item.put("name", new AttributeValue(createGymRequest.name));
        item.put("email", new AttributeValue(createGymRequest.email));
        item.put("username", new AttributeValue(createGymRequest.username));
        item.put("address", new AttributeValue(createGymRequest.address));
        item.put("sessionCapacity", new AttributeValue(createGymRequest.sessionCapacity));
        if (createGymRequest.foundingDay != null) { item.put("birthday", new AttributeValue(createGymRequest
                .foundingDay)); }
        if (createGymRequest.stripeID != null) { item.put("stripeID", new AttributeValue(createGymRequest.stripeID)); }
        if (createGymRequest.federatedID != null) { item.put("federatedID", new AttributeValue(createGymRequest
                .federatedID)); }
        if (createGymRequest.bio != null) { item.put("bio", new AttributeValue(createGymRequest.bio)); }
        if (createGymRequest.weeklyHours != null) { item.put("weeklyHours", new AttributeValue(Arrays.asList
                (createGymRequest.weeklyHours))); }
        if (createGymRequest.gymType != null) { item.put("gymType",
                new AttributeValue(createGymRequest.gymType)); }
        if (createGymRequest.paymentSplit != null) { item.put("paymentSplit",
                new AttributeValue(createGymRequest.paymentSplit)); }
        return new CreateDatabaseAction(itemType, item, passoverIdentifiers,
            (Map<String, AttributeValue> createdItem, String id) -> {
                return;
            }
        );
    }

//    public static DatabaseAction updateFoundingDay(String id, String foundingDay) throws Exception {
//        return UserDatabaseActionBuilder.updateBirthday(getPrimaryKey(id), foundingDay);
//    }

//    public static DatabaseAction updateEmail(String id, String email) throws Exception {
//        return UserDatabaseActionBuilder.updateEmail(getPrimaryKey(id), email);
//    }
//
//    public static DatabaseAction updateProfileImagePath(String id, String profileImagePath) throws Exception {
//        return UserDatabaseActionBuilder.updateProfileImagePath(getPrimaryKey(id), profileImagePath);
//    }
//
//    public static DatabaseAction updateAddScheduledWorkout(String id, String workout, boolean ifWithCreate) throws
//            Exception {
//        return UserDatabaseActionBuilder.updateAddScheduledWorkout(getPrimaryKey(id), workout, ifWithCreate);
//    }
//
//    public static DatabaseAction updateRemoveScheduledWorkout(String id, String workout) throws Exception {
//        return UserDatabaseActionBuilder.updateRemoveScheduledWorkout(getPrimaryKey(id), workout);
//    }
//
//    public static DatabaseAction updateAddCompletedWorkout(String id, String workout) throws Exception {
//        return UserDatabaseActionBuilder.updateAddCompletedWorkout(getPrimaryKey(id), workout);
//    }
//
//    public static DatabaseAction updateRemoveCompletedWorkout(String id, String workout) throws Exception {
//        return UserDatabaseActionBuilder.updateRemoveCompletedWorkout(getPrimaryKey(id), workout);
//    }

    // TODO How to combine this with the original UserActionDatabaseBuilder logic????
    public static DatabaseAction updateAddScheduledTime(String id, String time, boolean ifSchedulingWorkout) throws
            Exception {
        if (ifSchedulingWorkout) {
            TimeInterval timeInterval = new TimeInterval(time);
            return UserDatabaseActionBuilder.updateAddScheduledTime(id, itemType, time, new CheckHandler() {
                @Override
                public String isViable(DatabaseItem newItem) throws Exception {
                    // This is to check whether any times conflict
                    Gym gym = (Gym) newItem;

                    // Check every time section to see if it's too filled for the gym space.
                    for (TimeInterval timeSection : timeInterval.getAllTimeSections()) {
                        int numWorkouts = 0;
                        // TODO This is problematic because it's a string SET, so there's a chance a time will be lost
                        for (TimeInterval gymTime : gym.scheduledTimes) {
                            if (timeSection.intersects(gymTime)) {
                                numWorkouts++;
                            }
                        }

                        // If the number of workouts is at (or above lol) the capacity, then you can't add another one
                        if (numWorkouts >= gym.sessionCapacity) {
                            return "Gym is already at capacity for that time!";
                        }
                    }

                    // Has to be during the weekly time
                    for (TimeInterval weeklyTime : gym.weeklyHours) {
                        // TODO this needs to be fixed
//                        if (weeklyTime.weeklyEncompasses(timeInterval)) {
                            // Then make sure that it isn't during a vacation time
                            for (TimeInterval vacationTime : gym.vacationTimes) {
                                if (vacationTime.intersects(timeInterval)) {
                                    return "That workout is during one of the gym's vacation times!";
                                }
                            }

                            return null;
//                        }
                    }

                    return "That workout isn't during the gym's open hours!";
                }
            });
        }
        else {
            // TODO Should a gym be able to go to events?
            return UserDatabaseActionBuilder.updateAddScheduledTime(id, itemType, time, null);
        }
    }

//    public static DatabaseAction updateRemoveScheduledTime(String id, String time) throws Exception {
//        return UserDatabaseActionBuilder.updateRemoveScheduledTime(getPrimaryKey(id), time);
//    }

    public static DatabaseAction updateAddress(String id, String address) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "address", new AttributeValue(address), false, PUT);
    }

    public static DatabaseAction updateAddTrainer(String id, String trainer, boolean ifWithCreate) throws Exception {
        if (ifWithCreate) {
            return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "trainer", null, true, ADD);
        }
        else {
            return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "trainer", new AttributeValue(trainer), false, ADD);
        }

    }

    public static DatabaseAction updateRemoveTrainer(String id, String trainer) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "trainer", new AttributeValue(trainer), false, DELETE);
    }

    public static DatabaseAction updateWeeklyHours(String id, String[] weeklyHours) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "weeklyHours", new AttributeValue(Arrays.asList(weeklyHours)),
                false, PUT);
    }

    // TODO Consider going back to this
//    public static DatabaseAction updateAddWeeklyHour(String id, String weeklyHour) throws Exception {
//        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "weekly_hours", new AttributeValue(weeklyHour), false, ADD);
//    }
//
//    public static DatabaseAction updateRemoveWeeklyHour(String id, String weeklyHour) throws Exception {
//        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "weekly_hours", new AttributeValue(weeklyHour), false, "REMOVE");
//    }

    public static DatabaseAction updateAddVacationTime(String id, String vacationTime) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "vacationTimes", new AttributeValue(vacationTime), false, ADD);
    }

    public static DatabaseAction updateRemoveVacationTime(String id, String vacationTime) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "vacationTimes", new AttributeValue(vacationTime), false, DELETE);
    }

    public static DatabaseAction updateSessionCapacity(String id, String sessionCapacity) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "sessionCapacity", new AttributeValue(sessionCapacity), false,
                PUT);
    }

    public static DatabaseAction updateGymType(String id, String gymType) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "gymType", new AttributeValue(gymType), false, PUT);
    }

    public static DatabaseAction updatePaymentSplit(String id, String paymentSplit) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "paymentSplit", new AttributeValue(paymentSplit), false, PUT);
    }

    public static DatabaseAction delete(String id) {
        return new DeleteDatabaseAction(id, itemType, getPrimaryKey(id));
    }
}

