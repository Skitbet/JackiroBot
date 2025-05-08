package gay.skitbet.jackiro.model

import gay.skitbet.mongoy.annotation.CollectionName
import gay.skitbet.mongoy.annotation.IdField

@CollectionName("servers_name")
data class ServerConfig(
    @IdField
    var guildId: String = "",

    var botUpdateChannelId: String? = null,
    var disabledModules: ArrayList<String> = ArrayList(),
    var disabledCommands: MutableList<String> = mutableListOf<String>(),
    var userData: HashMap<String, ServerUserData> = HashMap(),

    // XP Settings
    var xpEnabled: Boolean = true,
    var xpMultiplier: Double = 1.0,
    var xpPerMessage: Int = 10,
    var cooldownSeconds: Int = 60,
    var baseXpToLevelUp: Int = 100
)
