package gay.skitbet.jackiro.task;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.JackiroLauncher;
import gay.skitbet.jackiro.utils.JackiroShards;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class UpdateStatusTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(UpdateStatusTask.class);  // Logger for better logging
    private static final long UPDATE_INTERVAL = 25000L;  // 25 seconds
    private long lastUpdate = System.currentTimeMillis();
    private int currentIndex = 0;

    public UpdateStatusTask() {
        super("Update Status Task");
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            try {
                updateStatus();
            } catch (Exception e) {
                logger.error("Error updating status: ", e);
            }
            Thread.sleep(1000);  // Sleep for 1 second before checking again
        }
    }

    private void updateStatus() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String uptimeStr = formatUptime(uptime);

        String status = Jackiro.config.getCurrentlyPlaying().get(currentIndex);

        JackiroShards jackiroShards = Jackiro.getInstance().getJackiroShards();
        status = status.replace("{guilds}", String.valueOf(jackiroShards.getGuilds().size()));
        status = status.replace("{users}", String.valueOf(jackiroShards.getUsers().size()));
        status = status.replace("{uptime}", uptimeStr);

        long diff = System.currentTimeMillis() - lastUpdate;

        if (diff >= UPDATE_INTERVAL) {
            jackiroShards.setPresence(OnlineStatus.ONLINE, Activity.playing(status));
            currentIndex = (currentIndex + 1) % Jackiro.config.getCurrentlyPlaying().size();
            lastUpdate = System.currentTimeMillis();
        }
    }

    private String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}
