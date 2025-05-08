package gay.skitbet.jackiro.command

import lombok.Getter
import lombok.Setter
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping

class CommandContext(private val event: SlashCommandInteractionEvent, command: Command) {
    val user: User = event.user
    val member: Member? = event.member
    val guild: Guild? = event.guild
    val channel: MessageChannelUnion = event.channel


    init {
        this.event.deferReply(command.ephemeral).queue()
    }

    fun reply(message: String) {
        event.hook.editOriginal(message).queue()
    }

    fun reply(embed: MessageEmbed) {
        event.hook.editOriginalEmbeds(embed).queue()
    }

    fun getOption(key: String): OptionMapping? {
        return event.getOption(key)
    }
}
