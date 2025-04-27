package gay.skitbet.jackiro.command;

import gay.skitbet.jackiro.command.impl.PingCommand;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
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
        Package commandPackage = this.getClass().getPackage();
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
        System.out.println(commands);
        for (Guild guild : shardManager.getGuilds()) {
            System.out.println("Loading commnads for " + guild);
            CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
            commandListUpdateAction.addCommands(
                    commands.values().stream().map(Command::toData).collect(Collectors.toSet())
            ).queue();
        }
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
            if (command.getPermission() != null && event.getMember().hasPermission(command.getPermission())) {
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
