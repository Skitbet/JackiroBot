package gay.skitbet.jackiro.listener

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.managers.LevelingManager
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.managers.SetupManager
import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MainListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val guildId = event.guild.id
        val session = SetupManager.getSetup(guildId)
        if (session != null) {
            session.handleMessage(event.message)
            return
        }

        val config = MongoManager.serverConfigRepository.findById(guildId)
        config?.let { LevelingManager.handleGainXP(it, event.author.id) }
    }

    override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
        if (event.user?.isBot == true) return

        val guildId = event.guild.id
        SetupManager.getSetup(guildId)?.handleReaction(event.reaction)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        event.deferEdit().queue()

        if (event.componentId.equals("jackiro_setup", ignoreCase = true)) {
            event.guild?.id?.let { guildId ->
                val session = SetupManager.getSetup(guildId)
                session?.handleDropDown(event.interaction.selectedOptions)
            }
        }
    }

    override fun onReady(event: ReadyEvent) {
        val instance = Jackiro.instance
        instance.readyShards++
        println("Shard ${event.jda.shardInfo.shardId} is ready!")

        if (instance.readyShards >= Jackiro.config.shardCount) {
            println("All shards are ready!")
            instance.onReady()
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        val guild = event.guild
        val serverConfig = gay.skitbet.jackiro.model.ServerConfig(guild.id)

        MongoManager.serverConfigRepository
            .save(serverConfig)
        guild.defaultChannel?.asTextChannel()?.sendMessageEmbeds(JackiroEmbed.getNewGuildEmbed(guild))?.queue()
        Jackiro.instance.commandHandler.registerGuildCommands(guild)

        guild.deafen(event.jda.selfUser, true).queue()
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        MongoManager.serverConfigRepository.deleteById(event.guild.id)
    }
}
