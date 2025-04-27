package gay.skitbet.jackiro.listener;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.managers.SetupManager;
import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.utils.SetupSession;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

public class MainListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        // Handle setup responses
        SetupSession session = SetupManager.getSetup(event.getGuild().getId());
        if (session != null) {
            session.handleMessage(event.getMessage());
            return;
        }

    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ShardManager shardManager = Jackiro.getInstance().getShardManager();
        Jackiro.getInstance().readyShards++;
        System.out.println("Shard " + event.getJDA().getShardInfo().getShardId() + " is ready!");

        if (Jackiro.getInstance().readyShards >= Jackiro.config.getShardCount()) {
            System.out.println("All shards are ready!");
            Jackiro.getInstance().onReady();
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        ServerConfig serverConfig = new ServerConfig(event.getGuild().getId());

        Jackiro.getInstance().getServerConfigRepository().save(serverConfig);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Jackiro.getInstance().getServerConfigRepository()
                .delete(event.getGuild().getId());
    }


}
