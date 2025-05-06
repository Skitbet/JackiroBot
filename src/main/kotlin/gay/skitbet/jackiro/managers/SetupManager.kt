package gay.skitbet.jackiro.managers

import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.utils.setup.SetupSession
import java.util.concurrent.ConcurrentHashMap

object SetupManager {
    private val activeSetups: MutableMap<String, SetupSession> = ConcurrentHashMap()

    fun startSetup(context: CommandContext): SetupSession {
        val setupSession = SetupSession(context)
        activeSetups[context.guild.id] = setupSession
        return setupSession
    }

    fun getSetup(guildId: String): SetupSession? {
        return activeSetups[guildId]
    }

    fun endSetup(setupSession: SetupSession) {
        activeSetups.remove(setupSession.channel.guild.id)
    }
}
