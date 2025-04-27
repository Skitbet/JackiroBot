package gay.skitbet.jackiro.utils;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class JackiroConfig {

    public String clientToken;
    public String clientID;
    public String clientSecret;
    public String ownerId;
    public int shardCount;
    public List<String> currentlyPlaying = new ArrayList<>();


}
