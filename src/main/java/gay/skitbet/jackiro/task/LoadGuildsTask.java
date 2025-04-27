package gay.skitbet.jackiro.task;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.model.ServerConfig;

public class LoadGuildsTask extends Thread {

    private final Jackiro jackiro;

    public LoadGuildsTask(Jackiro jackiro) {
        super("Update Forgotten Guilds Task");
        this.jackiro = jackiro;
    }

    @Override
    public void run() {
        for (var guild : jackiro.getJackiroShards().getGuilds()) {
            ServerConfig config = jackiro.getServerConfigRepository().load(guild.getId());
            if (config == null) {
                config = new ServerConfig(guild.getId());
                jackiro.getServerConfigRepository().save(config);
                Jackiro.LOGGER.info("Created default config for guild: " + guild.getName());
            } else {
                System.out.println("Loaded existed config for guild: " + guild.getName());
            }
        }
    }
}
