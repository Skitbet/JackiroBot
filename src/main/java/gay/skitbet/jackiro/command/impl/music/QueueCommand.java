package gay.skitbet.jackiro.command.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.music.GuildMusicManager;
import gay.skitbet.jackiro.music.JackiroMusicManager;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import gay.skitbet.jackiro.utils.JackiroModule;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;

public class QueueCommand extends Command {
    public QueueCommand() {
        super("queue", "Shows the current music track queue", JackiroModule.MUSIC, false);
    }

    @Override
    public void execute(CommandContext context) {
        GuildMusicManager manager = Jackiro.getInstance().getJackiroMusicManager().getGuildAndPlayer(context.getGuild());
        if (manager == null) {
            context.reply(new JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("No music playing!")
                    .setDescription("There is no music playing in this server.")
                    .build());
            return;
        }

        AudioTrack playingTrack = manager.player.getPlayingTrack();
        StringBuilder queueDisplay = new StringBuilder();

        // Display currently playing track
        if (playingTrack != null) {
            String duration = JackiroMusicManager.formatDuration(playingTrack.getDuration());
            queueDisplay.append("**Currently Playing**: ").append(playingTrack.getInfo().title)
                    .append(" (").append(duration).append(")\n");
        } else {
            queueDisplay.append("**Currently Playing**: None\n");
        }

        // Display next 10 tracks in the queue
        List<AudioTrack> queue = manager.scheduler.getQueue().stream().toList();
        if (queue.isEmpty()) {
            queueDisplay.append("\n**Queue is Empty**: There are no tracks currently in the queue.");
        } else {
            queueDisplay.append("\n**Next 10 tracks in queue**:\n");
            for (int i = 0; i < Math.min(queue.size(), 10); i++) {
                AudioTrack track = queue.get(i);
                String duration = JackiroMusicManager.formatDuration(track.getDuration());
                queueDisplay.append("**#").append(i + 1).append("**: ").append(track.getInfo().title)
                        .append(" (").append(duration).append(")\n");
            }
        }

        // Send the final message
        context.reply(new JackiroEmbed()
                .setColor(Color.CYAN)
                .setTitle("Current Track and Queue")
                .setDescription(queueDisplay.toString())
                .build());
    }


    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        return List.of();
    }
}
