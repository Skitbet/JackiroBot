package gay.skitbet.jackiro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gay.skitbet.jackiro.utils.JackiroConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class JackiroLauncher {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("./config.json");

    public static void main(String[] args) {
        JackiroConfig config = loadOrCreateConfig();
        Jackiro.initialize(config);
    }

    /**
     * Loads or creates a configuration for the bot!
     * @return
     */
    private static JackiroConfig loadOrCreateConfig() {
        if (CONFIG_FILE.exists()) {
            try {
                String json = FileUtils.readFileToString(CONFIG_FILE, StandardCharsets.UTF_8);
                return GSON.fromJson(json, JackiroConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to read config file: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            createDefaultConfig();
            System.out.println("\nConfig created! Please configure the `config.json` before restarting the bot.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Creates default config for the bot
     */
    private static void createDefaultConfig() {
        System.out.println("Jackiro Bot - First time setup");
        System.out.println("Creating a default config.json...");

        JackiroConfig defaultConfig = new JackiroConfig()
                .setClientSecret("client secret here")
                .setClientID("client ID here")
                .setClientToken("bot token here")
                .setOwnerId("Discord user ID here")
                .setCurrentlyPlaying(Collections.singletonList("Life"));

        try {
            FileUtils.writeStringToFile(CONFIG_FILE, GSON.toJson(defaultConfig), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to create default config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
