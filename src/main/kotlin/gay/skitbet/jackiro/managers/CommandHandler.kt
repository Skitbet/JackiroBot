package gay.skitbet.jackiro.managers

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.model.ServerConfig
import gay.skitbet.jackiro.utils.JackiroEmbed
import gay.skitbet.jackiro.utils.JackiroModule
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction
import net.dv8tion.jda.api.sharding.ShardManager
import org.jetbrains.annotations.NotNull
import org.reflections.Reflections

class CommandHandler(private val shardManager: ShardManager) : ListenerAdapter() {

    val commands: MutableMap<String, Command> = HashMap()

    init {
        shardManager.addEventListener(this)
        addCommands()
    }

    /**
     * Adds all commands to the HashMap<String, Command>
     */
    private fun addCommands() {
        val commandPackage = Command::class.java.`package`
        val packageName = commandPackage.name
        val reflections = Reflections("$packageName.impl")

        val commandClasses: Set<Class<out Command>> = reflections.getSubTypesOf(Command::class.java)
        for (commandClass in commandClasses) {
            try {
                val command = commandClass.getConstructor().newInstance()
                addCommand(command)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // adds command from command instance
    private fun addCommand(command: Command) {
        commands[command.name] = command
    }

    fun registerCommands() {
        for (guild in shardManager.guilds) {
            registerGuildCommands(guild)
        }
    }

    fun registerGuildCommands(guild: Guild) {
        val config = MongoManager.serverConfigRepository.findOrCreate(guild.id)

        val commandListUpdateAction = guild.updateCommands()
        commandListUpdateAction.addCommands(
            commands.values.filter { command ->
                val module = command.module
                module == null || !config.disabledModules.contains(module.name)
            }.map { it.toData() }.toSet()
        ).queue()
    }

    fun getCommand(name: String): Command? {
        return commands[name.lowercase()]
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val commandName = event.name.lowercase()
        val command = getCommand(commandName)
        if (event.guild == null) return

        if (command != null) {
            // handle if command is disabled
            val config = MongoManager.serverConfigRepository.findById(event.guild?.id)
            if (config == null) return
            if (config.disabledCommands.contains(command.name)) {
                event.replyEmbeds(JackiroEmbed().error("This command is disabled in this server!")).setEphemeral(true).queue()
                return
            }

            val context = CommandContext(event, command)

            // make sure the user has permission!
            if (command.permission != null && !event.member!!.hasPermission(command.permission)) {
                context.reply(JackiroEmbed().error("Invalid permission!"))
                return
            }

            // execute command
            try {
                command.execute(context)
            } catch (e: Exception) {
                context.reply(JackiroEmbed().error("An error occurred while executing the command."))
                e.printStackTrace()
            }
        } else {
            event.replyEmbeds(JackiroEmbed().error("Unknown command!")).queue()
        }
    }
}
