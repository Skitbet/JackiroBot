package gay.skitbet.jackiro.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {
    val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    fun queue(track: AudioTrack) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack()
        }
    }

    fun peekNextTrack(): AudioTrack? {
        return queue.peek()
    }

    fun getTrackPositionInQueue(track: AudioTrack): Int {
        var position = 0
        for (queuedTrack in queue) {
            if (queuedTrack == track) {
                return position
            }
            position++
        }
        return -1  // Return -1 if the track is not found
    }
}
