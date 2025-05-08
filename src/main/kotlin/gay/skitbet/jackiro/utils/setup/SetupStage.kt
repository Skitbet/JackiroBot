package gay.skitbet.jackiro.utils.setup

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption

abstract class SetupStage(
    val setupDescription: String,
    protected val stageReactions: List<Emoji> = emptyList()
) {
    protected lateinit var setupSession: SetupSession

    fun setupStage(session: SetupSession) {
        this.setupSession = session
    }

    open fun getActionRow(): ItemComponent? = null

    open fun handleActionRow(options: List<SelectOption>): Boolean = true

    abstract fun handleMessageReceived(message: Message): Boolean

    open fun handleReactionReceived(reaction: MessageReaction): Boolean = true
}
