package gay.skitbet.jackiro.model;

import gay.skitbet.mongoy.annotation.CollectionName;
import gay.skitbet.mongoy.annotation.IdField;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Accessors(fluent = true)
@ToString
@CollectionName("servers_name")
public class ServerConfig {

    @IdField
    public String guildId;

    public String botUpdateChannelId;
    public ArrayList<String> disabledModules = new ArrayList<>();

    public List<String> disabledCommands = new ArrayList<>();
    public HashMap<String, ServerUserData> userData = new HashMap<>();

    // XP Settings
    public boolean xpEnabled = true;
    public double xpMultiplier = 1.0;
    public int xpPerMessage = 10;
    public int cooldownSeconds = 60;
    public int baseXpToLevelUp = 100;

    public ServerConfig(String guildId) {
        this.guildId = guildId;
    }

    public ServerConfig() {}
}
