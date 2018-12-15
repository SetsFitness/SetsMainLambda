package main.java.lambdaFunctionHandlers;

import main.java.Logic.Constants;
import main.java.Logic.ItemType;
import main.java.databaseOperations.DatabaseActionCompiler;
import main.java.databaseOperations.DynamoDBHandler;
import main.java.lambdaFunctionHandlers.highLevelHandlers.createDependencyHandlers.*;
import main.java.lambdaFunctionHandlers.highLevelHandlers.deleteDependencyHandlers.*;
import main.java.lambdaFunctionHandlers.highLevelHandlers.readHandlers.*;
import main.java.lambdaFunctionHandlers.highLevelHandlers.updateAddDependencyHandlers.*;
import main.java.lambdaFunctionHandlers.highLevelHandlers.updateRemoveDependencyHandlers.*;
import main.java.lambdaFunctionHandlers.highLevelHandlers.updateSetDependencyHandlers.*;
import main.java.lambdaFunctionHandlers.requestObjects.*;
import main.java.lambdaFunctionHandlers.responseObjects.*;
import java.util.List;


// TODO MAKE SURE THAT ALL THE CONSTRUCTORS AND SETTERS/GETTERS ARE IN HERE
public class LambdaRequest {
    private String fromID;
    private String action;
    private String specifyAction;
    private String itemType;
    private String[] identifiers;
    private String attributeName;
    private String[] attributeValues;

    private CreateClientRequest createClientRequest;
    private CreateTrainerRequest createTrainerRequest;
    private CreateGymRequest createGymRequest;
    private CreateWorkoutRequest createWorkoutRequest;
    private CreateReviewRequest createReviewRequest;
    private CreateEventRequest createEventRequest;
    private CreateChallengeRequest createChallengeRequest;
    private CreateInviteRequest createInviteRequest;
    private CreatePostRequest createPostRequest;

    private enum Action {
        CREATE,
        READ,
        UPDATESET,
        UPDATEADD,
        UPDATEREMOVE,
        DELETE
    }

    private enum AttributeName {
        // User ===========================
        name,
        gender,
        birthday,
        foundingDay,
        email,
        username,
        profileImagePath,
        profileImagePaths,
        scheduledWorkouts,
        completedWorkouts,
        scheduledTimes,
        reviewsBy,
        reviewsAbout,
        friendlinessRating,
        effectivenessRating,
        reliabilityRating,
        bio,
        friends,
        friendRequests,
        scheduledEvents,
        invitedEvents,
        ownedEvents,
        sentInvites,
        receivedInvites,
        // Client ==========================
        trainersFollowing,
        // Trainer =========================
        gym,
        availableTimes,
        workoutSticker,
        preferredIntensity,
        workoutCapacity,
        workoutPrice,
        followers,
        subscriptionPrice,
        // Gym =============================
        address,
        trainers,
        weeklyHours,
        vacationTimes,
        sessionCapacity,
        gymType,
        paymentSplit,
        // Workout =========================
        time,
        trainer,
        clients,
        capacity,
        sticker,
        intensity,
        missingReviews,
        price,
        // Review ==========================
        by,
        about,
        description,
        // Event ===========================
        title,
        members,
        access,
        restriction,
        tags,
        challenge,
        // Challenge =======================
        goal,
        winner,
        difficulty,
        endTime,
        prize,
        // Invite ==========================
        // Post   ==========================
        picturePaths,
        videoPaths,
        // postType,
    }

    // This is where the inputs are handled!
    public Object process() throws Exception {
        try {
            // First check all of the inputs for the stuff
            checkInputs();

            switch (Action.valueOf(action)) {
                case CREATE:
                    if (specifyAction.equals("ForSurvey") && itemType.equals("Review")) {
                        // Create Review and remove from workout missingReviews
                        if (identifiers.length == 1) {
                            return handleSurveyCreate(identifiers[0]);
                        }
                        else {
                            throw new Exception("There should be 1 identifier when creating a Review for the survey!");
                        }
                    }
                    else if (specifyAction.equals("")) {
                        // Create the item
                        if (identifiers.length == 0) {
                            return handleCreate();
                        }
                        else {
                            throw new Exception("There should be no identifies for a normal CREATE statement!");
                        }
                    }
                    else {
                        // Unacceptable action series
                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
                                action + "\" on itemType \"" + itemType + "\" !");
                    }
                case READ:
                    throw new Exception("Reading with Lambda no longer supported! Just use GraphQL!");
//                    if (specifyAction.equals("ByID") || specifyAction.equals("")) {
//                        // Read using the ID number provided
//                        return handleIDRead(identifiers);
//                    }
//                    else if (specifyAction.equals("ByUsername")) {
//                        // Read using the username provided
//                        return handleUsernameRead(identifiers);
//                    }
//                    else if (specifyAction.equals("GetAll") && itemType.equals("Gym")) {
//                        // Read all of the gyms
//                        return handleGetAll();
//                    }
//                    else {
//                        // Unacceptable action series
//                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
//                                action + "\" on itemType \"" + itemType + "\" !");
//                    }
                case UPDATESET:
                    if (specifyAction.equals("")) {
                        // Update the attribute: SET it.
                        // There are a number of things this could be. Some require checking, some require multi-faceted
                        if (identifiers.length == 1) {
                            if (attributeName == null || attributeName.equals("") || attributeValues == null) {
                                throw new Exception("Need to set attributeName and attributeValues for update " +
                                        "statement");
                            }
                            handleUpdateSet(identifiers[0]);
                            return null;
                        }
                        else {
                            throw new Exception("There should only be one identifier in an UPDATESET statement!");
                        }
                    }
                    else {
                        // Unacceptable action series
                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
                                action + "\" on itemType \"" + itemType + "\" !");
                    }
                case UPDATEADD:
                    if (specifyAction.equals("")) {
                        // Update the attribute: ADD it.
                        // There are a number of things this could be. Some require checking, some require multi-faceted
                        if (identifiers.length == 1) {
                            if (attributeName == null || attributeName.equals("") || attributeValues == null) {
                                throw new Exception("Need to set attributeName and attributeValues for update " +
                                        "statement");
                            }
                            handleUpdateAdd(identifiers[0]);
                            return null;
                        }
                        else {
                            throw new Exception("There should only be one identifier in an UPDATEADD statement!");
                        }
                    }
                    else {
                        // Unacceptable action series
                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
                                action + "\" on itemType \"" + itemType + "\" !");
                    }
                case UPDATEREMOVE:
                    if (specifyAction.equals("")) {
                        // Update the attribute: REMOVE it.
                        // There are a number of things this could be. Some require checking, some require multi-faceted
                        if (identifiers.length == 1) {
                            if (attributeName == null || attributeName.equals("") || attributeValues == null) {
                                throw new Exception("Need to set attributeName and attributeValues for update " +
                                        "statement");
                            }
                            handleUpdateRemove(identifiers[0]);
                            return null;
                        }
                        else {
                            throw new Exception("There should only be one identifier in an UPDATEREMOVE statement!");
                        }
                    }
                    else {
                        // Unacceptable action series
                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
                                action + "\" on itemType \"" + itemType + "\" !");
                    }
                case DELETE:
                    if (specifyAction.equals("")) {
                        // Delete the item from the database.
                        if (identifiers.length == 1) {
                            handleDelete(identifiers[0]);
                            return null;
                        }
                        else {
                            throw new Exception("There should only be one identifier in a DELETE statement!");
                        }
                    }
                    else {
                        // Unacceptable action series
                        throw new Exception("Specify Action: \"" + specifyAction + "\" not recognized for action \"" +
                                action + "\" on itemType \"" + itemType + "\" !");
                    }
                default:
                    throw new Exception("Item type: " + itemType + " recognized but not handled?");
            }

        }
        catch (IllegalArgumentException e) {
            throw new Exception("Action: \"" + action + "\" not recognized! Error: " + e.getLocalizedMessage());
        }
    }

    private void checkInputs() throws Exception {
        // Action, ItemType, SpecifyAction, attributeName
        if (specifyAction == null) {
            specifyAction = "";
        }
        if (identifiers == null) {
            identifiers = new String[]{};
        }

        if (action != null  && fromID != null && !fromID.equals("") && itemType != null) {
            if (attributeName != null) {
                if (attributeName.equals("")) {
                    throw new Exception("No fields are allowed to be empty strings!");
                }
            }

            if (attributeValues != null) {
                for (String value : attributeValues) {
                    if (value.equals("")) {
                        throw new Exception("No fields are allowed to be empty strings!");
                    }
                }
            }
            int numCreateRequest = 0;
            if (createClientRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create client request!\n");
            }
            if (createTrainerRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create trainer request!\n");
            }
            if (createGymRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create gym request!\n");
            }
            if (createWorkoutRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create workout request!\n");
            }
            if (createReviewRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create review request!\n");
            }
            if (createEventRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create event request!\n");
            }
            if (createChallengeRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create challenge request!\n");
            }
            if (createInviteRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create invite request!\n");
            }
            if (createPostRequest != null) {
                numCreateRequest++;
                Constants.debugLog("Has a create post request!\n");
            }
            if (numCreateRequest > 1) {
                throw new Exception("Only one create request allowed at a time!");
            }
        }
        else {
            //throw new Exception("action = " + action + ", fromID = " + fromID + ", itemType = " + itemType +  "!");
            throw new Exception("action, fromID, and itemType fields must be inputted");
        }
    }

    private String handleCreate() throws Exception {
        try {
            switch (ItemType.valueOf(itemType)) {
                case Client:
                    return CreateClient.handle(fromID, createClientRequest);
                case Trainer:
                    return CreateTrainer.handle(fromID, createTrainerRequest);
                case Gym:
                    return CreateGym.handle(fromID, createGymRequest);
                case Workout:
                    return CreateWorkout.handle(fromID, createWorkoutRequest);
                case Review:
                    // Send a null for surveyWorkoutID because we're not creating it for a survey
                    return CreateReview.handle(fromID, createReviewRequest, null);
                case Event:
                    return CreateEvent.handle(fromID, createEventRequest);
                case Challenge:
                    return CreateChallenge.handle(fromID, createChallengeRequest);
                case Invite:
                    return CreateInvite.handle(fromID, createInviteRequest);
                case Post:
                    return CreatePost.handle(fromID, createPostRequest);
                default:
                    throw new Exception("Item Type: " + itemType + " recognized but not handled?");
            }
        }
        catch (IllegalArgumentException e) {
            throw new Exception("Item Type: " + itemType + " not recognized! Error: " + e.getLocalizedMessage());
        }
    }

    private String handleSurveyCreate(String workoutID) throws Exception {
        return CreateReview.handle(fromID, createReviewRequest, workoutID);
    }

//    private List<ObjectResponse> handleIDRead(String[] ids) throws Exception {
//        try {
//            switch (ItemType.valueOf(itemType)) {
//                case Client:
//                    return ReadClientsByID.handle(ids);
//                case Trainer:
//                    return ReadTrainersByID.handle(ids);
//                case Gym:
//                    return ReadGymsByID.handle(ids);
//                case Workout:
//                    return ReadWorkoutsByID.handle(ids);
//                case Review:
//                    return ReadReviewsByID.handle(ids);
//                case Event:
//                    return ReadEventsByID.handle(ids);
//                case Challenge:
//                    return ReadChallengesByID.handle(ids);
//                case Invite:
//                    return ReadInvitesByID.handle(ids);
//                case Post:
//                    return ReadPostsByID.handle(ids);
//                default:
//                    throw new Exception("Item Type: " + itemType + " recognized but not handled?");
//            }
//        }
//        catch (IllegalArgumentException e) {
//            throw new Exception("Item Type: " + itemType + " not recognized! Error: " + e.getLocalizedMessage());
//        }
//    }

//    private List<ObjectResponse> handleUsernameRead(String[] usernames) throws Exception {
//        try {
//            switch (ItemType.valueOf(itemType)) {
//                case Client:
//                    return ReadClientsByUsername.handle(usernames);
//                case Trainer:
//                    return ReadTrainersByUsername.handle(usernames);
//                case Gym:
//                    return ReadGymsByUsername.handle(usernames);
//                case Workout:
//                    throw new Exception("Can't query a workout by a username!");
//                case Review:
//                    throw new Exception("Can't query a review by a username!");
//                case Event:
//                    throw new Exception("Can't query an event by a username!");
//                case Challenge:
//                    throw new Exception("Can't query a challenge by a username!");
//                case Invite:
//                    throw new Exception("Can't query an invite by a username!");
//                case Post:
//                    throw new Exception("Can't query a post by a username!");
//                default:
//                    throw new Exception("Item Type: " + itemType + " recognized but not handled?");
//            }
//        }
//        catch (IllegalArgumentException e) {
//            throw new Exception("Item Type: " + itemType + " not recognized! Error: " + e.getLocalizedMessage());
//        }
//    }

//    private List<ObjectResponse> handleGetAll() throws Exception {
//        // Already Checked, so go to town
//        return ReadAllGyms.handle();
//    }

    public void handleUpdateSet(String id) throws Exception {
        // switch all attributes, then if necessary, item type
        DatabaseActionCompiler databaseActionCompiler = new DatabaseActionCompiler();

        String attributeValue = null;
        if (attributeValues.length != 1 && attributeValues.length != 0 && !attributeName.equals("weeklyHours")) {
            throw new Exception("For updating " + attributeName + " on " + itemType + "attributeValues must be only " +
                    "0 or 1 item long!");
        }
        else if (attributeValues.length == 0 && attributeName.equals("weeklyHours")) {
            // DynamoDB can't handle empty string sets
            attributeValues = null;
        }
        else {
            attributeValue = attributeValues[0];
        }

        ItemType type = ItemType.valueOf(itemType);

        try {
            switch (AttributeName.valueOf(attributeName)) {
                case name:
                    // TODO Change all of these into switch statements?
                    switch (type) {
                        case Client:
                        case Trainer:
                        case Gym:
                            databaseActionCompiler.addAll(UserUpdateName.getActions(fromID, id, itemType, attributeValue));
                            break;
                        default:
                            throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case gender:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserUpdateGender.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case birthday:
                    if (itemType.equals("Client") || itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(UserUpdateBirthday.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case foundingDay:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdateFoundingDay.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case email:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserUpdateEmail.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case profileImagePath:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserUpdateProfileImagePath.getActions(fromID, id, itemType,
                                attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case bio:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserUpdateBio.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case workoutSticker:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerUpdateWorkoutSticker.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case preferredIntensity:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerUpdatePreferredIntensity.getActions(fromID, id,
                                attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case workoutCapacity:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerUpdateWorkoutCapacity.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case workoutPrice:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerUpdateWorkoutPrice.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case subscriptionPrice:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerUpdateSubscriptionPrice.getActions(fromID, id,
                                attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case address:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdateAddress.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateAddress.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case weeklyHours:
                    // TODO I want to handle this differently in the future
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdateWeeklyHours.getActions(fromID, id, attributeValues));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case sessionCapacity:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdateSessionCapacity.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case gymType:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdateGymType.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case paymentSplit:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymUpdatePaymentSplit.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case title:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateTitle.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateTitle.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case description:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateDescription.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateDescription.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostUpdateDescription.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case access:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateAccess.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateAccess.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostUpdateAccess.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case restriction:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateRestriction.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateRestriction.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case challenge:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateChallenge.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case goal:
                    if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateGoal.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case capacity:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventUpdateCapacity.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateCapacity.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case difficulty:
                    if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateDifficulty.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case endTime:
                    if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateEndTime.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case prize:
                    if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdatePrize.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                case winner:
                    if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeUpdateWinner.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " + itemType + "!");
                    }
                    break;
                default:
                    throw new Exception("Can't perform an UPDATESET operation on " + attributeName + "!");
            }
        }
        catch (IllegalArgumentException e) {
            throw new Exception("AttributeName: " + attributeName + " not recognized! Error: " + e.getLocalizedMessage());
        }

        DynamoDBHandler.getInstance().attemptTransaction(databaseActionCompiler);
    }

    public void handleUpdateAdd(String id) throws Exception {
        // switch all attributes, then if necessary, item type
        DatabaseActionCompiler databaseActionCompiler = new DatabaseActionCompiler();

        String attributeValue = null;
        if (attributeValues.length != 1 && attributeValues.length != 0) {
            throw new Exception("For updating " + attributeName + " on " + itemType + "attributeValues must be 0 or " +
                    "1 item long!");
        }
        else if (attributeValues.length == 1) {
            // DynamoDB can't handle empty string sets
            attributeValue = attributeValues[0];
        }

        try {
            switch (AttributeName.valueOf(attributeName)) {
                case profileImagePaths:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserAddProfileImagePath.getActions(fromID, id,
                                itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case scheduledWorkouts:
                    if (itemType.equals("Client")) {
                        databaseActionCompiler.addAll(ClientAddToWorkout.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case friends:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserAddFriend.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case scheduledEvents:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserAddToEvent.getActions(fromID, id, itemType, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case availableTimes:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerAddAvailableTime.getActions(fromID, id,
                                attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case vacationTimes:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymAddVacationTime.getActions(fromID, id,
                                attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case tags:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventAddTag.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeAddTag.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case picturePaths:
                    if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostAddPicturePath.getActions(fromID, id, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case videoPaths:
                    if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostAddVideoPath.getActions(fromID, id, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                default:
                    throw new Exception("Can't perform an UPDATEADD operation on " + attributeName + "!");
            }
        }
        catch (IllegalArgumentException e) {
            throw new Exception("AttributeName: " + attributeName + " not recognized! Error: " + e.getLocalizedMessage());
        }

        DynamoDBHandler.getInstance().attemptTransaction(databaseActionCompiler);
    }

    public void handleUpdateRemove(String id) throws Exception {
        // switch all attributes, then if necessary, item type
        DatabaseActionCompiler databaseActionCompiler = new DatabaseActionCompiler();

        String attributeValue = null;
        if (attributeValues.length != 1 && attributeValues.length != 0) {
            throw new Exception("For updating " + attributeName + " on " + itemType + "attributeValues must be 0 or " +
                    "1 item long!");
        }
        else if (attributeValues.length == 1) {
            // DynamoDB can't handle empty string sets
            attributeValue = attributeValues[0];
        }

        try {
            switch (AttributeName.valueOf(attributeName)) {
                case profileImagePaths:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserRemoveProfileImagePath.getActions(fromID, id,
                                itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case scheduledWorkouts:
                    if (itemType.equals("Client")) {
                        databaseActionCompiler.addAll(ClientRemoveFromWorkout.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case friends:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserRemoveFriend.getActions(fromID, id, itemType, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case scheduledEvents:
                    if (itemType.equals("Client") || itemType.equals("Trainer") || itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(UserRemoveFromEvent.getActions(fromID, id, itemType, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case availableTimes:
                    if (itemType.equals("Trainer")) {
                        databaseActionCompiler.addAll(TrainerRemoveAvailableTime.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case vacationTimes:
                    if (itemType.equals("Gym")) {
                        databaseActionCompiler.addAll(GymRemoveVacationTime.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                case tags:
                    if (itemType.equals("Event")) {
                        databaseActionCompiler.addAll(EventRemoveTag.getActions(fromID, id, attributeValue));
                    }
                    else if (itemType.equals("Challenge")) {
                        databaseActionCompiler.addAll(ChallengeRemoveTag.getActions(fromID, id, attributeValue));
                    }
                    else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case picturePaths:
                    if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostRemovePicturePath.getActions(fromID, id, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                case videoPaths:
                    if (itemType.equals("Post")) {
                        databaseActionCompiler.addAll(PostRemoveVideoPath.getActions(fromID, id, attributeValue));
                    } else {
                        throw new Exception("Unable to perform " + action + " to " + attributeName + " for a " +
                                itemType + "!");
                    }
                    break;
                default:
                    throw new Exception("Can't perform an UPDATEREMOVE operation on " + attributeName + "!");
            }
        }
        catch (IllegalArgumentException e) {
            throw new Exception("AttributeName: " + attributeName + " not recognized! Error: " + e.getLocalizedMessage());
        }

        DynamoDBHandler.getInstance().attemptTransaction(databaseActionCompiler);
    }

    public void handleDelete(String id) throws Exception {
        DatabaseActionCompiler databaseActionCompiler = new DatabaseActionCompiler();

        //Constants.debugLog("Handling delete actions");
        try {
            switch (ItemType.valueOf(itemType)) {
                case Client:
                    databaseActionCompiler.addAll(DeleteClient.getActions(fromID, id));
                    break;
                case Trainer:
                    databaseActionCompiler.addAll(DeleteTrainer.getActions(fromID, id));
                    break;
                case Gym:
                    databaseActionCompiler.addAll(DeleteGym.getActions(fromID, id));
                    break;
                case Workout:
                    databaseActionCompiler.addAll(DeleteWorkout.getActions(fromID, id));
                    break;
                case Review:
                    databaseActionCompiler.addAll(DeleteReview.getActions(fromID, id));
                    break;
                case Event:
                    databaseActionCompiler.addAll(DeleteEvent.getActions(fromID, id));
                    break;
                case Challenge:
                    databaseActionCompiler.addAll(DeleteChallenge.getActions(fromID, id));
                    break;
                case Invite:
                    databaseActionCompiler.addAll(DeleteInvite.getActions(fromID, id));
                    break;
                case Post:
                    databaseActionCompiler.addAll(DeletePost.getActions(fromID, id));
                    break;
                default:
                    throw new Exception("Item Type: " + itemType + " recognized but not handled?");
            }
        }
        catch (IllegalArgumentException e) {
            throw new Exception("Item Type: \"" + itemType + "\" not recognized! Error: " + e.getLocalizedMessage());
        }

        //Constants.debugLog("Attempting the transaction");
        DynamoDBHandler.getInstance().attemptTransaction(databaseActionCompiler);
    }

    public LambdaRequest(String fromID, String action, String specifyAction, String itemType, String[] identifiers, String
            attributeName, String[] attributeValues, CreateClientRequest createClientRequest, CreateTrainerRequest
            createTrainerRequest, CreateGymRequest createGymRequest, CreateWorkoutRequest createWorkoutRequest,
                         CreateReviewRequest createReviewRequest, CreateEventRequest createEventRequest,
                         CreateChallengeRequest createChallengeRequest, CreateInviteRequest createInviteRequest) {
        this.fromID = fromID;
        this.action = action;
        this.specifyAction = specifyAction;
        this.itemType = itemType;
        this.identifiers = identifiers;
        this.attributeName = attributeName;
        this.attributeValues = attributeValues;
        this.createClientRequest = createClientRequest;
        this.createTrainerRequest = createTrainerRequest;
        this.createGymRequest = createGymRequest;
        this.createWorkoutRequest = createWorkoutRequest;
        this.createReviewRequest = createReviewRequest;
        this.createEventRequest = createEventRequest;
        this.createChallengeRequest = createChallengeRequest;
        this.createInviteRequest = createInviteRequest;
    }

    public LambdaRequest() {}

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSpecifyAction() {
        return specifyAction;
    }

    public void setSpecifyAction(String specifyAction) {
        this.specifyAction = specifyAction;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(String[] identifiers) {
        this.identifiers = identifiers;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String[] getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(String[] attributeValues) {
        this.attributeValues = attributeValues;
    }

    public CreateClientRequest getCreateClientRequest() {
        return createClientRequest;
    }

    public void setCreateClientRequest(CreateClientRequest createClientRequest) {
        this.createClientRequest = createClientRequest;
    }

    public CreateTrainerRequest getCreateTrainerRequest() {
        return createTrainerRequest;
    }

    public void setCreateTrainerRequest(CreateTrainerRequest createTrainerRequest) {
        this.createTrainerRequest = createTrainerRequest;
    }

    public CreateGymRequest getCreateGymRequest() {
        return createGymRequest;
    }

    public void setCreateGymRequest(CreateGymRequest createGymRequest) {
        this.createGymRequest = createGymRequest;
    }

    public CreateWorkoutRequest getCreateWorkoutRequest() {
        return createWorkoutRequest;
    }

    public void setCreateWorkoutRequest(CreateWorkoutRequest createWorkoutRequest) {
        this.createWorkoutRequest = createWorkoutRequest;
    }

    public CreateReviewRequest getCreateReviewRequest() {
        return createReviewRequest;
    }

    public void setCreateReviewRequest(CreateReviewRequest createReviewRequest) {
        this.createReviewRequest = createReviewRequest;
    }

    public CreateEventRequest getCreateEventRequest() {
        return createEventRequest;
    }

    public void setCreateEventRequest(CreateEventRequest createEventRequest) {
        this.createEventRequest = createEventRequest;
    }

    public CreateChallengeRequest getCreateChallengeRequest() {
        return createChallengeRequest;
    }

    public void setCreateChallengeRequest(CreateChallengeRequest createChallengeRequest) {
        this.createChallengeRequest = createChallengeRequest;
    }

    public CreateInviteRequest getCreateInviteRequest() {
        return createInviteRequest;
    }

    public void setCreateInviteRequest(CreateInviteRequest createInviteRequest) {
        this.createInviteRequest = createInviteRequest;
    }
}
