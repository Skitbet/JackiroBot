package gay.skitbet.jackiro.command;

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

    private Permission permission;

    public Command(String name, String description) {
        this.name = name.toLowerCase();
        this.description = description;
    }

    public Command(String name, String description, Permission permission) {
        this.name = name.toLowerCase();
        this.description = description;
        this.permission = permission;
    }

    public abstract void execute(CommandContext context);
    public abstract List<OptionData> addOptions(List<OptionData> options);

    public CommandData toData() {
        List<OptionData> options = addOptions(new ArrayList<>());

        return new CommandDataImpl(name, description)
                .addOptions(options);
    }
}
