package gay.skitbet.jackiro.listener;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.utils.JackiroShards;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MainListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JackiroShards jackiroShards = Jackiro.getInstance().getJackiroShards();
        jackiroShards.readyShards++;
        System.out.println("Shard " + event.getJDA().getShardInfo().getShardId() + " is ready!");

        if (jackiroShards.readyShards >= Jackiro.config.getShardCount()) {
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
