package gay.skitbet.jackiro.command.impl;

import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PingCommand extends Command {
    public PingCommand() {
        super("ping", "PONG PONG PONG");
    }

    @Override
    public void execute(CommandContext context) {
        context.reply("PONGG!");
    }

    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        return options;
    }
}
