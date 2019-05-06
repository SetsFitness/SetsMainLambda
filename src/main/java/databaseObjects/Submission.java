package main.java.databaseObjects;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Submission represents a specific posting to a Challenge that represents an attempt to win the
 * Challenge. Acts like a special Post right now.
 */
public class Submission extends DatabaseObject {
    public String by;
    public String description;
    public String about;
    public Set<String> picturePaths;
    public Set<String> videoPaths;
    public Set<String> likes;
    public Set<String> comments;

    /**
     * The main constructor for the Submission class, instantiating the object from the database.
     *
     * @param item The {@link Item} object obtained from the database query/fetch.
     * @throws Exception If anything goes wrong with the translation.
     */
    public Submission(Item item) throws Exception {
        super(item);
        this.by = item.getString("by");
        this.description = item.getString("description");
        this.about = item.getString("about");
        this.picturePaths = item.getStringSet("picturePaths");
        if (picturePaths == null) { this.picturePaths = new HashSet<>(); }
        this.videoPaths = item.getStringSet("videoPaths");
        if (videoPaths == null) { this.videoPaths = new HashSet<>(); }
        this.likes = item.getStringSet("likes");
        if (likes == null) { this.likes = new HashSet<>(); }
        this.comments = item.getStringSet("comments");
        if (comments == null) { this.comments = new HashSet<>(); }
    }

    /**
     * Gets the empty item with the default values for the Submission object.
     *
     * @return The map of attribute values for the item.
     */
    public static Map<String, AttributeValue> getEmptyItem() {
        Map<String, AttributeValue> item = DatabaseObject.getEmptyItem();
        item.put("item_type", new AttributeValue("Submission"));
        return item;
    }

    /**
     * Reads a Submission from the database using the given ID.
     *
     * TODO Implement cache system here again?
     *
     * @param id The ID to read from the database.
     * @return The Submission object to read in the database.
     * @throws Exception If anything goes wrong in the fetch.
     */
    public static Submission readSubmission(String id) throws Exception {
        return (Submission) read(tableName, getPrimaryKey("Submission", id));
    }
}
