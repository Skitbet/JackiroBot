package gay.skitbet.jackiro.command.impl;

import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.managers.SetupManager;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import gay.skitbet.jackiro.utils.SetupSession;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SetupCommand extends Command {
    public SetupCommand() {
        super("setup", "Run the quick setup on Jackiro for this Guild!", Permission.ADMINISTRATOR, null);
    }

    @Override
    public void execute(CommandContext context) {
        SetupSession session = SetupManager.getSetup(context.getGuild().getId());
        if (session != null) {
            context.reply(new JackiroEmbed().error("A setup is already in progress!"));
            return;
        }

        SetupManager.startSetup(context);

    }

    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        return List.of();
    }
}
