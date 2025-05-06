package gay.skitbet.jackiro.task

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.model.ServerConfig
import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LoadGuildsTask(private val jackiro: Jackiro) {

    fun start(scheduler: ScheduledExecutorService) {
        scheduler.schedule(::loadGuilds, 0, TimeUnit.SECONDS)
    }

    fun loadGuilds() {
        val shardManager = jackiro.shardManager

        for (guild in shardManager.guilds) {
            val repo = MongoManager.serverConfigRepository
            var config = repo.findById(guild.id)

            if (config == null) {
                config = ServerConfig(guild.id)
                repo.save(config)
                Jackiro.logger.info("Created default config for guild: ${guild.name}")

                (guild.defaultChannel as? TextChannel)?.let { channel ->
                    channel.sendMessageEmbeds(JackiroEmbed.getNewGuildEmbed(guild)).queue()
                }
            } else {
                Jackiro.logger.info("Loaded existing config for guild: ${guild.name}")
            }
        }
    }
}
