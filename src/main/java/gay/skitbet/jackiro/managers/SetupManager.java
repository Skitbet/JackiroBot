package gay.skitbet.jackiro.managers;

import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.utils.SetupSession;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SetupManager {
    private static final Map<String, SetupSession> activeSetups = new ConcurrentHashMap<>();

    public static SetupSession startSetup(CommandContext context) {
        SetupSession setupSession = new SetupSession(context);
        activeSetups.put(context.getGuild().getId(), setupSession);
        return setupSession;
    }

    public static SetupSession getSetup(String guildId) {
        return activeSetups.get(guildId);
    }

    public static void endSetup(SetupSession setupSession) {
        activeSetups.remove(setupSession.getChannel().getGuild().getId());
    }
}
