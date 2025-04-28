package gay.skitbet.jackiro.command;

import gay.skitbet.jackiro.utils.JackiroModule;
import lombok.Data;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Command {
    private final String name;
    private final String description;
    private final JackiroModule module;

    private Permission permission;

    public Command(String name, String description, JackiroModule module) {
        this.name = name.toLowerCase();
        this.description = description;
        this.module = module;
    }

    public Command(String name, String description, Permission permission, JackiroModule module) {
        this.name = name.toLowerCase();
        this.description = description;
        this.permission = permission;
        this.module = module;
    }

    public abstract void execute(CommandContext context);
    public abstract List<OptionData> addOptions(List<OptionData> options);

    public CommandData toData() {
        List<OptionData> options = addOptions(new ArrayList<>());

        return new CommandDataImpl(name, description)
                .addOptions(options);
    }
}
