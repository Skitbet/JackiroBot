package gay.skitbet.jackiro.utils.setup

import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.managers.SetupManager
import gay.skitbet.jackiro.model.ServerConfig
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.components.selections.SelectOption

class SetupSession(context: CommandContext) {

    internal val member: Member? = context.member
    internal val channel: TextChannel = context.channel.asTextChannel()
    internal val config: ServerConfig? = MongoManager.serverConfigRepository.findById(context.guild?.id)

    internal var currentStage: SetupStage? = StageRegistry.STAGES.firstOrNull()
    internal var lastQuestion: Message? = null
    internal var lastResponse: Message? = null

    init {
        startSession()
    }

    private fun startSession() {
        channel.sendMessage("⏳ Setting up, please wait...").queue { msg ->
            lastQuestion = msg
            askNextQuestion()
        }
    }

    fun handleMessage(message: Message) {
        lastResponse = message
        if (currentStage?.handleMessageReceived(message) == true) {
            advanceStage()
        }
    }

    fun handleReaction(reaction: MessageReaction) {
        if (currentStage?.handleReactionReceived(reaction) == true) {
            advanceStage()
        }
    }

    fun handleDropDown(options: List<SelectOption>) {
        if (currentStage?.handleActionRow(options) == true) {
            advanceStage()
        }
    }

    private fun advanceStage() {
        currentStage = StageRegistry.nextStep(currentStage)
        askNextQuestion()
    }

    private fun askNextQuestion() {
        val stage = currentStage
        if (stage != null && StageRegistry.getStepIndex(stage) < StageRegistry.STAGES.size) {
            stage.setupStage(this)
            deleteLastQuestion()

            channel.sendMessage(stage.setupDescription).apply {
                stage.getActionRow()?.let { setActionRow(it) }
            }.queue { msg -> lastQuestion = msg }
        } else {
            finish()
        }
    }

    private fun deleteLastQuestion() {
        lastQuestion?.delete()?.queue()
        lastResponse?.delete()?.queue()
        lastResponse = null
    }

    private fun finish() {
        config?.let { MongoManager.serverConfigRepository.save(it) }
        SetupManager.endSetup(this)
        deleteLastQuestion()

        channel.sendMessage(
            "✅ Setup complete! Thank you for setting up **Jackiro**. You can now use the bot as intended!"
        ).queue()
    }
}
