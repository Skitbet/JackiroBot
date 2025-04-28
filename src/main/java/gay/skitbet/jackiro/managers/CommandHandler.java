package gay.skitbet.jackiro.managers;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import gay.skitbet.jackiro.utils.JackiroModule;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, Command> commands;
    private final ShardManager shardManager;

    public CommandHandler(ShardManager shardManager) {
        this.shardManager = shardManager;
        this.commands = new HashMap<>();
        shardManager.addEventListener(this);
        addCommands();
    }

    /**
     * Adds all commands to the HashMap<String, Command>
     */
    public void addCommands() {
        Package commandPackage = Command.class.getPackage();
        String packageName = commandPackage.getName();
        Reflections reflections = new Reflections(packageName + ".impl");

        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> commandClass : commandClasses) {
            try {
                Command command = commandClass.getConstructor().newInstance();
                addCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // adds commadn from command instance
    public void addCommand(Command command) {
        this.commands.put(command.getName(), command);
    }


    public void registerCommands() {
        for (Guild guild : shardManager.getGuilds()) {
            registerGuildCommands(guild);
        }
    }

    public void registerGuildCommands(Guild guild) {
        ServerConfig config = Jackiro.getInstance().getServerConfigRepository().load(guild.getId());

        CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
        commandListUpdateAction.addCommands(
                commands.values().stream()
                        .filter(command -> {
                            JackiroModule module = command.getModule();
                            return module == null || !config
                                    .disabledModules.contains(module.name());
                        })
                        .map(Command::toData)
                        .collect(Collectors.toSet())
        ).queue();
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName().toLowerCase();
        Command command = getCommand(commandName);

        if (command != null) {
            CommandContext context = new CommandContext(event);

            // make sure the user has permission!
            if (command.getPermission() != null && !event.getMember().hasPermission(command.getPermission())) {
                context.reply(new JackiroEmbed().error("Invalid permission!"));
                return;
            }

            // execute command
            try {
                command.execute(context);
            } catch (Exception e) {
                context.reply(new JackiroEmbed().error("An error occurred while executing the command."));
                e.printStackTrace();
            }
        } else {
            event.replyEmbeds(new JackiroEmbed().error("Unknown command!")).queue();
        }
    }
}
