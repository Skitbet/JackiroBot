package gay.skitbet.jackiro.task

import gay.skitbet.jackiro.Jackiro
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class UpdateStatusTask {

    private val logger = LoggerFactory.getLogger(UpdateStatusTask::class.java)
    private var currentIndex = 0
    private var lastUpdate = System.currentTimeMillis()

    companion object {
        private const val UPDATE_INTERVAL_MS = 25_000L
    }

    fun start(scheduler: ScheduledExecutorService) {
        scheduler.scheduleAtFixedRate(::updateStatusSafely, 0, 1, TimeUnit.SECONDS)
    }

    private fun updateStatusSafely() {
        try {
            updateStatus()
        } catch (e: Exception) {
            logger.error("Error updating status", e)
        }
    }

    private fun updateStatus() {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val uptimeStr = formatUptime(uptime)

        val statusTemplates = Jackiro.config.currentlyPlaying
        if (statusTemplates.isEmpty()) return

        val statusTemplate = statusTemplates[currentIndex]
        val shardManager = Jackiro.instance.shardManager

        val filledStatus = statusTemplate
            .replace("{guilds}", shardManager.guilds.size.toString())
            .replace("{users}", shardManager.users.size.toString())
            .replace("{uptime}", uptimeStr)

        val now = System.currentTimeMillis()
        if (now - lastUpdate >= UPDATE_INTERVAL_MS) {
            shardManager.setPresence(OnlineStatus.ONLINE, Activity.playing(filledStatus))
            currentIndex = (currentIndex + 1) % statusTemplates.size
            lastUpdate = now
        }
    }

    private fun formatUptime(uptimeMillis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(uptimeMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60
        return "${days}d ${hours}h ${minutes}m ${seconds}s"
    }
}
