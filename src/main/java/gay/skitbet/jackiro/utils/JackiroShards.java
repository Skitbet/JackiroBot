package gay.skitbet.jackiro.utils;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.Presence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.prefs.PreferenceChangeEvent;

public class JackiroShards {

    public int readyShards = 0;

    @Getter
    private List<JDA> shards = new ArrayList<>();

    public Guild getGuildByID(String id) {
        for (JDA shard : shards) {
            var guild = shard.getGuildById(id);
            if (guild != null) { return guild; }
        }
        return null;
    }

    public List<Guild> getGuilds() {
        var guilds = new ArrayList<Guild>();

        for (JDA shard : shards) {
            guilds.addAll(shard.getGuilds());
        }
        return guilds;
    }

    public List<User> getUsers() {
        var users = new ArrayList<User>();

        for (JDA shard : shards) {
            users.addAll(shard.getUsers());
        }

        return users;
    }

    public User getUser(String id) {
        for (JDA shard : shards) {
            var user = shard.getUserById(id);
            if (user != null) return user;
        }

        return null;
    }

    public Presence getPresence() {
        return shards.get(0).getPresence();
    }

    public void setPresence(OnlineStatus status, Activity activity) {
        for (JDA shard : shards) {
            shard.getPresence().setPresence(status, activity);
        }
    }


}
