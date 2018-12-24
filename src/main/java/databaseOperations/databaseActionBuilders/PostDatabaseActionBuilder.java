package main.java.databaseOperations.databaseActionBuilders;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import main.java.databaseObjects.Post;
import main.java.databaseOperations.*;
import main.java.lambdaFunctionHandlers.requestObjects.CreatePostRequest;

import java.util.*;

public class PostDatabaseActionBuilder {
    final static private String itemType = "Post";

    public static DatabaseAction create(CreatePostRequest createPostRequest) {
        // Handle the setting of the items
        Map<String, AttributeValue> item = Post.getEmptyItem();
        item.put("by", new AttributeValue(createPostRequest.by));
        item.put("description", new AttributeValue(createPostRequest.description));
        item.put("access", new AttributeValue(createPostRequest.access));
        if (createPostRequest.postType != null) { item.put("postType", new AttributeValue(createPostRequest.postType)); }
        if (createPostRequest.about != null) { item.put("about", new AttributeValue(createPostRequest.about)); }
        if (createPostRequest.picturePaths != null) { item.put("picturePaths", new AttributeValue(Arrays.asList
                (createPostRequest.picturePaths))); }
        if (createPostRequest.videoPaths != null) { item.put("videoPaths", new AttributeValue(Arrays.asList
                (createPostRequest.videoPaths))); }
        return new CreateDatabaseAction(item, new UpdateWithIDHandler() {
            @Override
            public void updateWithID(Map<String, AttributeValue> item, String id) throws Exception {
                if (createPostRequest.picturePaths != null && createPostRequest.picturePaths.length > 0) {
                    List<String> picturePaths = new ArrayList<>();
                    for (String picturePath : createPostRequest.picturePaths) {
                        picturePaths.add(id + "/" + picturePath);
                    }
                    item.put("picturePaths", new AttributeValue(picturePaths));
                }
                if (createPostRequest.videoPaths != null && createPostRequest.videoPaths.length > 0) {
                    List<String> videoPaths = new ArrayList<>();
                    for (String videoPath : createPostRequest.videoPaths) {
                        videoPaths.add(id + "/" + videoPath);
                    }
                    item.put("videoPaths", new AttributeValue(videoPaths));
                }
            }
        });
    }

    public static DatabaseAction updateAccess(String id, String access) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "access", new AttributeValue(access), false, "PUT");
    }

    public static DatabaseAction updateDescription(String id, String description) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "description", new AttributeValue(description), false, "PUT");
    }

    public static DatabaseAction updateAddPicturePath(String id, String picturePath) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "picturePaths", new AttributeValue(picturePath), false, "ADD");
    }

    public static DatabaseAction updateRemovePicturePath(String id, String picturePath) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "picturePaths", new AttributeValue(picturePath), false, "DELETE");
    }

    public static DatabaseAction updateAddVideoPath(String id, String videoPath) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "videoPaths", new AttributeValue(videoPath), false, "ADD");
    }

    public static DatabaseAction updateRemoveVideoPath(String id, String videoPath) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "videoPaths", new AttributeValue(videoPath), false, "DELETE");
    }

    public static DatabaseAction updateAddLike(String id) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "likes", new AttributeValue().withN("1"), false, "ADD");
    }

    public static DatabaseAction updateRemoveLike(String id) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "likes", new AttributeValue().withN("1"), false, "DELETE");
    }

    public static DatabaseAction updateAddComment(String id, String comment, boolean ifWithCreate) throws Exception {
        if (ifWithCreate) {
            return new UpdateDatabaseAction(id, itemType, "comments", null, true, "ADD");
        }
        else {
            return new UpdateDatabaseAction(id, itemType, "comments", new AttributeValue(comment), false, "ADD");
        }
    }

    public static DatabaseAction updateRemoveComment(String id, String comment) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "comments", new AttributeValue(comment), false, "DELETE");
    }

    public static DatabaseAction updateGroup(String id, String group) throws Exception {
        return new UpdateDatabaseAction(id, itemType, "group", new AttributeValue(group), false, "PUT");
    }

    public static DatabaseAction delete(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("item_type", new AttributeValue(itemType));
        key.put("id", new AttributeValue(id));
        return new DeleteDatabaseAction(key);
    }
}
