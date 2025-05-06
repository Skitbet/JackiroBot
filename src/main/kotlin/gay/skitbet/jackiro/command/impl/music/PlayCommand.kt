package gay.skitbet.jackiro.command.impl.music

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.utils.JackiroEmbed
import gay.skitbet.jackiro.utils.JackiroModule
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color

class PlayCommand : Command("play", "Play media by a url!", JackiroModule.MUSIC, false) {
    override fun execute(context: CommandContext) {
        val url = context.getOption("url")!!.asString

        if (!context.member!!.voiceState!!.inAudioChannel() || context.member.voiceState!!
                .channel !is VoiceChannel
        ) {
            context.reply(
                JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("❌ No Channel!")
                    .setDescription("Sorry, but I cant a channel if your not in one!")
                    .build()
            )
            return
        }

        if (url.contains("youtube.com") && context.user.id != Jackiro.config.ownerId) {
            context.reply(
                JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("❌ YouTube Music Unavailable")
                    .setDescription("Sorry, YouTube music currently doesn't work. Please try again later! For now, you can use platforms like SoundCloud, Bandcamp, and Twitch.")
                    .build()
            )
            return
        }
        Jackiro.getInstance().jackiroMusicManager.loadAndPlay(context, url)
    }

    override fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?> {
        options.add(OptionData(OptionType.STRING, "url", "Url of media to play", true))
        return options
    }
}
