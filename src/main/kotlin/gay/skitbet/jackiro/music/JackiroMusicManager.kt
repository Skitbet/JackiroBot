package gay.skitbet.jackiro.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import java.awt.Color

class JackiroMusicManager : ListenerAdapter() {

    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<String, GuildMusicManager> = HashMap()

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
        Jackiro.getInstance().shardManager.addEventListener(this)
    }

    @Synchronized
    fun getGuildAndPlayer(guild: Guild, musicChannel: TextChannel): GuildMusicManager {
        var musicManager = musicManagers[guild.id]

        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager, musicChannel)
            musicManagers[guild.id] = musicManager
        }

        guild.audioManager.setSendingHandler(musicManager.getSendHandler())
        return musicManager
    }

    fun loadAndPlay(context: CommandContext, trackUrl: String) {
        val musicManager = getGuildAndPlayer(context.guild, context.channel.asTextChannel())

        playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(audioTrack: AudioTrack) {
                // Prepare the track info for the embed message
                val duration = formatDuration(audioTrack.duration)
                val nextTrack = musicManager.scheduler.peekNextTrack()?.info?.title ?: "No track in queue."

                // Send the track info embed
                context.reply(JackiroEmbed()
                    .setColor(Color.GREEN)
                    .setTitle("Track Added")
                    .setDescription("**${audioTrack.info.title}** has been added to the queue!")
                    .addField("Duration", duration, true)
                    .addField("Next Track", nextTrack, true)
                    .setThumbnailUrl(audioTrack.info.uri)
                    .build())

                play(context, context.guild, musicManager, audioTrack)
            }

            override fun playlistLoaded(audioPlaylist: AudioPlaylist) {
                var firstTrack = audioPlaylist.selectedTrack
                if (firstTrack == null) {
                    firstTrack = audioPlaylist.tracks[0]
                }

                val duration = formatDuration(firstTrack!!.duration)
                val nextTrack = musicManager.scheduler.peekNextTrack()?.info?.title ?: "No track in queue."

                // Send playlist info
                context.reply(JackiroEmbed()
                    .setColor(Color.GREEN)
                    .setTitle("Playlist Loaded")
                    .setDescription("Added **${firstTrack.info.title}** from playlist **${audioPlaylist.name}** to the queue.")
                    .addField("Duration", duration, true)
                    .addField("Next Track", nextTrack, true)
                    .setThumbnailUrl(firstTrack.info.uri)
                    .build())

                play(context, context.guild, musicManager, firstTrack)
            }

            override fun noMatches() {
                context.reply(JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("No Results Found")
                    .setDescription("Couldn't find anything for **$trackUrl**. Please check the URL and try again.")
                    .build())
            }

            override fun loadFailed(e: FriendlyException) {
                val errorMessage = "Could not load track: ${e.message}"
                context.reply(JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("Load Failed")
                    .setDescription(errorMessage)
                    .build())
                e.printStackTrace()
            }
        })
    }

    private fun play(context: CommandContext, guild: Guild, musicManager: GuildMusicManager, track: AudioTrack) {
        if (musicManager.player.playingTrack != null) {
            val position = musicManager.scheduler.getTrackPositionInQueue(track)
            val nextTrack = musicManager.scheduler.peekNextTrack()?.info?.title ?: "No track in queue."

            var musicChannel: TextChannel? = musicManager.musicChannel
            if (musicChannel == null) {
                musicChannel = guild.defaultChannel?.asTextChannel()
            }
            val currentTrack = musicManager.player.playingTrack.info
            musicChannel?.sendMessageEmbeds(JackiroEmbed()
                .setColor(Color.YELLOW)
                .setTitle("Track Added to Queue")
                .setDescription("The track **${currentTrack.title}** is already playing! It's now in the queue.")
                .addField("Position in Queue", position.toString(), true)
                .addField("Next Track", nextTrack, true)
                .build())?.queue()
        }

        context.member.voiceState?.channel?.asVoiceChannel()?.let {
            connectToVoiceChannel(it, guild.audioManager)
            musicManager.scheduler.queue(track)
        }

    }

    private fun connectToVoiceChannel(voiceChannel: VoiceChannel, audioManager: AudioManager) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }

    companion object {
        fun formatDuration(durationMillis: Long): String {
            val minutes = durationMillis / 60000
            val seconds = (durationMillis % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}
