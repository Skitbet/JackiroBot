package gay.skitbet.jackiro.managers

import gay.skitbet.jackiro.model.ServerConfig
import gay.skitbet.jackiro.model.ServerUserData
import java.time.Instant

/*
LevelManager for handling level and xp on users!
Mostly static class as it's got no data I need linked to it
 */
object LevelingManager {

    fun handleGainXP(config: ServerConfig, userId: String): Boolean {
        val userData = config.userData[userId] ?: ServerUserData()

        val now = Instant.now().epochSecond
        val cooldown = config.cooldownSeconds

        if (now - userData.lastXpGainTimestamp < cooldown) {
            return false // too soon to gain XP
        }

        // update
        userData.xp = userData.xp + config.xpPerMessage
        userData.lastXpGainTimestamp = now

        // check for level up
        val newLevel = calculateLevel(userData.xp, config.baseXpToLevelUp)
        if (newLevel > userData.level) {
            userData.level = newLevel
        }

        config.userData[userId] = userData
        MongoManager.serverConfigRepository.save(config)
        return true
    }

    fun calculateLevel(xp: Int, baseXp: Int): Int {
        var level = 0
        var xpNeeded = baseXp

        var remainingXp = xp
        while (remainingXp >= xpNeeded) {
            remainingXp -= xpNeeded
            level++
            xpNeeded += baseXp / 2
        }

        return level
    }

    fun xpForNextLevel(level: Int, baseXp: Int): Int {
        var xpNeeded = 0
        var required = baseXp

        for (i in 0 until level) {
            xpNeeded += required
            required += baseXp / 2
        }

        return xpNeeded
    }
}
