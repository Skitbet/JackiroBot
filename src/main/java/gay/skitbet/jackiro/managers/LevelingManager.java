package gay.skitbet.jackiro.managers;

import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.model.ServerUserData;

import java.time.Instant;

/*
LevelManager for handling level and xp on users!
Mostly static class as its got no data i need linked to it
 */
public class LevelingManager {


    public static boolean handleGainXP(ServerConfig config, String userId) {
        ServerUserData userData = config.userData().getOrDefault(userId, new ServerUserData());

        long now = Instant.now().getEpochSecond();
        int cooldown = config.cooldownSeconds();

        if (now - userData.lastXpGainTimestamp() < cooldown) {
            return false; // to soon to gain xp
        }

        // update
        userData.xp(userData.xp() + config.xpPerMessage());
        userData.lastXpGainTimestamp(now);

        // check for level up
        int newLevel = calculateLevel(userData.xp(), config.baseXpToLevelUp());
        if (newLevel > userData.level()) {
            userData.level(newLevel);
        }

        config.userData().put(userId, userData);
        MongoManager.getServerConfigRepository().save(config);
        return true;
    }

    public static int calculateLevel(int xp, int baseXp) {
        int level = 0;
        int xPNeeded = baseXp;

        while (xp >= xPNeeded) {
            xp -= xPNeeded;
            level++;
            xPNeeded += baseXp / 2;
        }

        return level;
    }

    public static int xpForNextLevel(int level, int baseXp) {
        int xpNeeded = 0;
        int required = baseXp;

        for (int i = 0; i < level; i++) {
            xpNeeded += required;
            required += baseXp / 2;
        }

        return xpNeeded;
    }

}
