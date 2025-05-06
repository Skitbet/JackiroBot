package gay.skitbet.jackiro.utils.setup

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.managers.CommandHandler
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.managers.SetupManager
import gay.skitbet.jackiro.model.ServerConfig
import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class SetupSession(context: CommandContext) {
    internal val member: Member = context.member
    internal val channel: TextChannel = context.channel.asTextChannel()
    internal val config: ServerConfig = MongoManager.serverConfigRepository.findById(context.guild.id)
    internal var currentStage: SetupStage? = StageRegistry.STAGES.first()
    internal var questionMessage: Message? = null

    init {
        startSession()
    }

    private fun startSession() {
        channel.sendMessage("⏳ Setting up, please wait...").queue { msg ->
            questionMessage = msg
            askNextQuestion()
        }
    }

    fun handleMessage(message: Message) {
        if (currentStage?.handleMessageReceived(message) == true) {
            currentStage = StageRegistry.nextStep(currentStage!!) // advance after handling
            askNextQuestion()
        }
    }

    fun handleReaction(reaction: MessageReaction) {
        if (currentStage?.handleReactionReceived(reaction) == true) {
            currentStage = StageRegistry.nextStep(currentStage!!) // advance after handling
            askNextQuestion()
        }
    }

    private fun askNextQuestion() {
        if (currentStage != null && StageRegistry.getStepIndex(currentStage!!) < StageRegistry.STAGES.size) {
            currentStage!!.setupStage(this)
            val question = currentStage!!.setupDescription
            editQuestion(question)
        } else {
            finish()
        }
    }

    private fun editQuestion(content: String) {
        questionMessage?.let {
            if (currentStage?.getActionRow() != null) {
                it.editMessage(content).setActionRow(currentStage?.getActionRow()).queue { updated -> questionMessage = updated }
                return
            }
            it.editMessage(content).setComponents().queue { updated -> questionMessage = updated }
        }
    }

    private fun isFinished() = currentStage == null

    private fun finish() {
        MongoManager.serverConfigRepository.save(config)
        SetupManager.endSetup(this)

        questionMessage?.editMessage("✅ Setup complete! Thank you for setting up **Jackiro**. You can now use the bot as intended!")
            ?.setEmbeds()
            ?.queue()
    }

    fun sendCommandDropdown() {
        val commandHandler = Jackiro.getInstance().commandHandler
        val menuBuilder = StringSelectMenu.create("setup_command_select")
            .setPlaceholder("Select commands to disable")
            .setMinValues(1)
            .setMaxValues(commandHandler.commands.size.coerceAtLeast(1)) // at least 1

        commandHandler.commands.forEach { (name, command) ->
            menuBuilder.addOption(name, name)
        }

        questionMessage?.editMessageEmbeds(
            JackiroEmbed()
                .setDescription("🚫 Please select the commands you would like to disable.")
                .build()
        )
            ?.setActionRow(menuBuilder.build())
            ?.queue { updated -> questionMessage = updated }
    }

    fun handleDisableCommands(selectedOptions: List<SelectOption>) {
        config.disabledCommands.clear()
        selectedOptions.forEach { option ->
            config.disabledCommands.add(option.value)
        }

        MongoManager.serverConfigRepository.save(config)
        Jackiro.getInstance().commandHandler.registerGuildCommands(member.guild)

        if (isFinished()) {
            finish()
        } else {
            askNextQuestion()
        }
    }
}
