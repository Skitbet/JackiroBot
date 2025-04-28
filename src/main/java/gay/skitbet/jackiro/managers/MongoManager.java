package gay.skitbet.jackiro.managers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import gay.skitbet.jackiro.database.ServerConfigRepository;
import lombok.Getter;

public class MongoManager {

    private static MongoClient mongoClient;

    @Getter
    private static MongoDatabase database;

    @Getter
    public static ServerConfigRepository serverConfigRepository;


    public static void connect(String dbName) {
        mongoClient = MongoClients.create();
        database = mongoClient.getDatabase(dbName);
        initRepositories();
    }

    public static void initRepositories() {
        MongoManager.serverConfigRepository = new ServerConfigRepository(); // setup repo
    }

}
