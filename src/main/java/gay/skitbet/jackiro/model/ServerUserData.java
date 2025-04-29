package gay.skitbet.jackiro.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@ToString
public class ServerUserData {

    private int xp;
    private int level;
    private long lastXpGainTimestamp;
}
