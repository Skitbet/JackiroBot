package gay.skitbet.jackiro.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * This is a wrapper around AudioPlayer which makes it behave as an AudioSendHandler for JDA.
 * As JDA calls canProvide before every call to provide20MsAudio(), we pull the frame in canProvide()
 * and use the frame we already pulled in provide20MsAudio().
 */
class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {

    private val buffer: ByteBuffer = ByteBuffer.allocate(1024)
    private val frame: MutableAudioFrame = MutableAudioFrame().apply {
        setBuffer(buffer)
    }

    override fun canProvide(): Boolean {
        return audioPlayer.provide(frame)
    }

    override fun provide20MsAudio(): ByteBuffer {
        buffer.flip()
        return buffer
    }

    override fun isOpus(): Boolean {
        return true
    }
}
