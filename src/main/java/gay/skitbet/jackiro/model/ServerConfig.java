package gay.skitbet.jackiro.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@Accessors(fluent = true)
@ToString
public class ServerConfig {
    public String guildId;

    public ArrayList<String> disabledCommands = new ArrayList<>();

    public HashMap<String, ServerUserData> userData = new HashMap<>();

    public ServerConfig(String guildId) {
        this.guildId = guildId;
    }

    public ServerConfig() {}
}
