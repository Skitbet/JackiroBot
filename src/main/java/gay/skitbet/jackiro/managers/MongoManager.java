package gay.skitbet.jackiro.managers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import gay.skitbet.jackiro.database.ServerConfigRepository;
import gay.skitbet.mongoy.Mongoy;
import lombok.Getter;

public class MongoManager {

    @Getter
    public static ServerConfigRepository serverConfigRepository;


    public static void connect(String dbName) {
        Mongoy.init(dbName);
        initRepositories();
    }

    public static void initRepositories() {
        MongoManager.serverConfigRepository = new ServerConfigRepository(); // setup repo
    }

}
