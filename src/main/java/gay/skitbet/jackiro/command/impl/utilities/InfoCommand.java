package gay.skitbet.jackiro.command.impl.utilities;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import gay.skitbet.jackiro.utils.JackiroModule;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info", "Shows the current bot status and information!", JackiroModule.UTILITIES);
    }

    @Override
    public void execute(CommandContext context) {
        JackiroEmbed embed = new JackiroEmbed();
        embed.setTitle("✅ Jackiro Information & Status");
        embed.setDescription("Jackiro is a general purpose discord bot to help run things smoothly and fun! Manage everything from within Discord without the need of a panel! (Though one will be added soon)");
        embed.addField("🛠 Uptime:", getUptime(), true);
        embed.addField("🦊 Members:", String.valueOf(Jackiro.getInstance().getShardManager().getUsers().size() + 1), true);
        embed.addField("🎉 Guilds:", String.valueOf(Jackiro.getInstance().getShardManager().getGuilds().size()), true);
        embed.addField("💕 Invite:", "Coming soon", true);
        context.reply(embed.build());
    }

    private String getUptime() {
        Duration uptime = Duration.between(Jackiro.getInstance().getStartTime(), Instant.now());

        long days = uptime.toDays();
        long hours = uptime.toHours() % 24;
        long minutes = uptime.toMinutes() % 60;
        long seconds = uptime.getSeconds() % 60;

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }

    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        return List.of();
    }
}
