package gay.skitbet.jackiro.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gay.skitbet.jackiro.managers.MongoManager;
import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.model.ServerUserData;
import lombok.Getter;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public class ServerConfigRepository {

    private final MongoCollection<Document> collection;

    @Getter
    private final Map<String, ServerConfig> cache;

    public ServerConfigRepository() {
        this.collection = MongoManager.getDatabase().getCollection("server_configs");
        this.cache = new ConcurrentHashMap<>(); // Thread-safe map for caching
    }

    /**
     * Saves the server configuration to both cache and MongoDB.
     * @param config The ServerConfig to save.
     */
    public void save(ServerConfig config) {
        cache.put(config.guildId, config);
        Document doc = new Document()
                .append("_id", config.guildId)
                .append("botUpdateChannelID", config.botUpdateChannelId)
                .append("disabledCommands", config.disabledCommands)
                .append("userData", serializeUserData(config.userData));

        collection.replaceOne(Filters.eq("_id", config.guildId), doc, new ReplaceOptions().upsert(true));
    }

    /**
     * Deletes the server configuration from both cache and MongoDB.
     * @param guildId The guildId of the server configuration to delete.
     */
    public void delete(String guildId) {
        cache.remove(guildId);
        collection.deleteMany(Filters.eq("_id", guildId));
    }

    /**
     * Loads the server configuration from the cache or MongoDB.
     * @param guildId The guildId of the server configuration to load.
     * @return The ServerConfig if found, or new default if none exists
     */
    public ServerConfig load(String guildId) {
        // First check cache
        ServerConfig config = cache.get(guildId);

        // probably a better way of doing this lmao
        if (config == null) {
            config = loadFromDatabase(guildId);
        }

        if (config == null) {
            config = new ServerConfig(guildId);
        }


        return config;
    }

    /**
     * Helper method to load ServerConfig from the database.
     * @param guildId The guildId of the server configuration to load.
     * @return The loaded ServerConfig.
     */
    private ServerConfig loadFromDatabase(String guildId) {
        Document doc = collection.find(Filters.eq("_id", guildId)).first();
        if (doc == null) {
            return null;
        }

        ServerConfig config = new ServerConfig(doc.getString("_id"));
        config.botUpdateChannelId = doc.getString("botUpdateChannelID");
        config.disabledCommands.addAll(doc.getList("disabledCommands", String.class));

        loadUserData(config, doc);

        cache.put(guildId, config); // Cache the loaded config
        return config;
    }

    /**
     * Loads user data from a MongoDB document.
     * @param config The ServerConfig to populate.
     * @param doc The MongoDB document.
     */
    private void loadUserData(ServerConfig config, Document doc) {
        Document userDataDoc = (Document) doc.get("userData");
        if (userDataDoc != null) {
            for (String userId : userDataDoc.keySet()) {
                Document userDoc = (Document) userDataDoc.get(userId);
                ServerUserData userData = new ServerUserData();
                userData.xp(userDoc.getInteger("xp", 0)); // Default to 0 if not found
                config.userData.put(userId, userData);
            }
        }
    }

    /**
     * Serializes user data into a MongoDB-compatible document format.
     * @param userData The user data to serialize.
     * @return The serialized user data document.
     */
    private Document serializeUserData(Map<String, ServerUserData> userData) {
        Document userDataDoc = new Document();
        for (Map.Entry<String, ServerUserData> entry : userData.entrySet()) {
            userDataDoc.append(entry.getKey(), new Document("xp", entry.getValue().getXp()));
        }
        return userDataDoc;
    }
}
