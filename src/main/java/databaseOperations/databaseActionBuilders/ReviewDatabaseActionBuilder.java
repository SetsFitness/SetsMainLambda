package main.java.databaseOperations.databaseActionBuilders;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import main.java.databaseObjects.Review;
import main.java.databaseOperations.*;
import main.java.lambdaFunctionHandlers.requestObjects.CreateReviewRequest;

import java.util.Map;

import static main.java.databaseOperations.UpdateDatabaseAction.UpdateAction.*;

public class ReviewDatabaseActionBuilder {
    final static private String itemType = "Review";

    private static PrimaryKey getPrimaryKey(String id) {
        return new PrimaryKey("item_type", itemType, "id", id);
    }

    public static DatabaseAction create(CreateReviewRequest createReviewRequest, boolean ifWithCreate) {
        // Handle the setting of the items
        Map<String, AttributeValue> item = Review.getEmptyItem();
        item.put("by", new AttributeValue(createReviewRequest.by));
        item.put("about", new AttributeValue(createReviewRequest.about));
        item.put("friendlinessRating", new AttributeValue(createReviewRequest.friendlinessRating));
        item.put("effectivenessRating", new AttributeValue(createReviewRequest.effectivenessRating));
        item.put("reliabilityRating", new AttributeValue(createReviewRequest.reliabilityRating));
        item.put("description", new AttributeValue(createReviewRequest.description));
        return new CreateDatabaseAction(itemType, item, ifWithCreate,
            (Map<String, AttributeValue> createdItem, String id) -> {
                return;
            }
        );
    }

    public static DatabaseAction updateDescription(String id, String description) throws Exception {
        return new UpdateDatabaseAction(id, itemType, getPrimaryKey(id), "description", new AttributeValue(description), false, PUT);
    }

    public static DatabaseAction delete(String id) {
        return new DeleteDatabaseAction(id, itemType, getPrimaryKey(id));
    }
}
