package gay.skitbet.jackiro.command.impl.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.music.GuildMusicManager
import gay.skitbet.jackiro.music.JackiroMusicManager
import gay.skitbet.jackiro.utils.JackiroEmbed
import gay.skitbet.jackiro.utils.JackiroModule
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color
import kotlin.math.min
import kotlin.streams.toList

class QueueCommand : Command("queue", "Shows the current music track queue", JackiroModule.MUSIC, false) {
    override fun execute(context: CommandContext) {
        val manager: GuildMusicManager? = Jackiro.instance.jackiroMusicManager
            .getGuildAndPlayer(context.guild!!, context.channel.asTextChannel())
        if (manager == null) {
            context.reply(
                JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("No music playing!")
                    .setDescription("There is no music playing in this server.")
                    .build()
            )
            return
        }

        val playingTrack = manager.player.playingTrack
        val queueDisplay = StringBuilder()

        // Display currently playing track
        if (playingTrack != null) {
            val duration: String? = JackiroMusicManager.formatDuration(playingTrack.duration)
            queueDisplay.append("**Currently Playing**: ").append(playingTrack.info.title)
                .append(" (").append(duration).append(")\n")
        } else {
            queueDisplay.append("**Currently Playing**: None\n")
        }

        // Display next 10 tracks in the queue
        val queue: List<AudioTrack> = manager.scheduler.queue.stream().toList()
        if (queue.isEmpty()) {
            queueDisplay.append("\n**Queue is Empty**: There are no tracks currently in the queue.")
        } else {
            queueDisplay.append("\n**Next 10 tracks in queue**:\n")
            for (i in 0 until min(queue.size.toDouble(), 10.0).toInt()) {
                val track = queue[i]
                val duration: String? = JackiroMusicManager.formatDuration(track.duration)
                queueDisplay.append("**#").append(i + 1).append("**: ").append(track.info.title)
                    .append(" (").append(duration).append(")\n")
            }
        }

        // Send the final message
        context.reply(
            JackiroEmbed()
                .setColor(Color.CYAN)
                .setTitle("Current Track and Queue")
                .setDescription(queueDisplay.toString())
                .build()
        )
    }


    override fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?> {
        return mutableListOf()
    }
}
