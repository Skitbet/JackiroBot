package gay.skitbet.jackiro.task;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.managers.MongoManager;
import gay.skitbet.jackiro.model.ServerConfig;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LoadGuildsTask extends Thread {

    private final Jackiro jackiro;

    public LoadGuildsTask(Jackiro jackiro) {
        super("Update Forgotten Guilds Task");
        this.jackiro = jackiro;
    }

    @Override
    public void run() {
        for (var guild : jackiro.getShardManager().getGuilds()) {
            ServerConfig config = MongoManager.getServerConfigRepository().findById(guild.getId());
            if (config == null) {
                config = new ServerConfig(guild.getId());
                MongoManager.getServerConfigRepository().save(config);
                Jackiro.LOGGER.info("Created default config for guild: " + guild.getName());

                if (guild.getDefaultChannel() instanceof TextChannel textChannel) {
                    textChannel.sendMessageEmbeds(JackiroEmbed.getNewGuildEmbed(guild)).queue();
                }
            } else {
                System.out.println("Loaded existed config for guild: " + guild.getName());
            }
        }
    }
}
