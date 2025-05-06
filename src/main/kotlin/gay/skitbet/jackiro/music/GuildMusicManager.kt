package gay.skitbet.jackiro.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

/**
 * Holder for both the player and a track scheduler for one guild.
 */
class GuildMusicManager(
    manager: AudioPlayerManager,
    val musicChannel: TextChannel
) {

    /**
     * Audio player for the guild.
     */
    val player: AudioPlayer = manager.createPlayer()

    /**
     * Track scheduler for the player.
     */
    val scheduler: TrackScheduler = TrackScheduler(player).apply {
        player.addListener(this)
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    fun getSendHandler(): AudioPlayerSendHandler {
        return AudioPlayerSendHandler(player)
    }
}
