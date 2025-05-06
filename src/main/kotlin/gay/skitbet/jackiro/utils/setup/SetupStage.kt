package gay.skitbet.jackiro.utils.setup

import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ItemComponent

abstract class SetupStage(
    val setupDescription: String,
    protected val stageReactions: MutableList<Emoji> = mutableListOf()
) {

    protected lateinit var setupSession: SetupSession

    fun setupStage(setupSession: SetupSession) {
        this.setupSession = setupSession
    }

    open fun getActionRow(): ItemComponent? = null

    open fun handleActionRow(event: StringSelectInteractionEvent): Boolean = true

    abstract fun handleMessageReceived(message: Message): Boolean

    open fun handleReactionReceived(messageReaction: MessageReaction): Boolean = true
}
