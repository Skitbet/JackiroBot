package gay.skitbet.jackiro;

import gay.skitbet.jackiro.managers.CommandHandler;
import gay.skitbet.jackiro.managers.MongoManager;
import gay.skitbet.jackiro.database.ServerConfigRepository;
import gay.skitbet.jackiro.listener.MainListener;
import gay.skitbet.jackiro.music.JackiroMusicManager;
import gay.skitbet.jackiro.task.LoadGuildsTask;
import gay.skitbet.jackiro.task.UpdateStatusTask;
import gay.skitbet.jackiro.utils.JackiroConfig;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class Jackiro {

    private static Jackiro instance;

    // random utils
    public static final Random RANDOM = new Random();
    public static final Logger LOGGER = LoggerFactory.getLogger(Jackiro.class);
    public static JackiroConfig config;

    // important bot stuff
    private final ScheduledExecutorService executor;
    private final Instant startTime = Instant.now();
    private JackiroMusicManager jackiroMusicManager;
    private CommandHandler commandHandler;

    // db repositories
    private ServerConfigRepository serverConfigRepository;

    // jda shard manager
    public int readyShards;
    private ShardManager shardManager;


    public Jackiro(JackiroConfig config) {
        instance = this;
        Jackiro.config = config; // load config early
        this.executor = Executors.newScheduledThreadPool(8); // create async executor
    }

    /**
     * Gets the Jackiro instance.
     * @return Jackiro instance
     */
    public static Jackiro getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Jackiro has not been initialized yet.");
        }
        return instance;
    }

    /**
     * Boots up the bot and starts everything.
     * @param config config to use
     */
    public static void initialize(JackiroConfig config) {
        if (instance != null) {
            throw new IllegalStateException("Jackiro has already been initialized.");
        }
        new Jackiro(config).start();
    }

    /**
     * Starts up the bot (db, shards, etc).
     */
    private void start() {
        try {
            initializeMongoDB(); // connect to mongo
            initializeShardManager(); // start shards
            this.commandHandler = new CommandHandler(shardManager);
            LOGGER.info("Jackiro bot has started successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to start Jackiro: ", e);
            shutdown();
        }
    }

    /**
     * Connects to MongoDB database.
     */
    private void initializeMongoDB() {
        LOGGER.info("Connecting to MongoDB...");
        MongoManager.connect("jackiro"); // connect to db
        this.serverConfigRepository = new ServerConfigRepository(); // setup repo
        LOGGER.info("MongoDB connected.");
    }

    /**
     * Initializes JDA's ShardManager and loads shards.
     */
    private void initializeShardManager() {
        LOGGER.info("Initializing Shard Manager...");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.getClientToken())
                .setShardsTotal(config.getShardCount())
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.listening("beep boop .. loading")) // funny starting status
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(new MainListener()); //  register main listener

        shardManager = builder.build();

    }

    /**
     * Called when shards are ready.
     * Starts background tasks like status updates and guild loading.
     */
    public void onReady() {
        new UpdateStatusTask().start(); // auto update status
        new LoadGuildsTask(this).start(); // cache guild settings
        this.commandHandler.registerCommands();
        this.jackiroMusicManager = new JackiroMusicManager();
    }

    /**
     * Shuts down the bot and kills everything safely.
     */
    public void shutdown() {
        LOGGER.warn("Shutting down Jackiro...");
        if (shardManager != null) {
            shardManager.shutdown(); // shutdown shards
        }
        executor.shutdownNow(); // stop async tasks
        System.exit(1); // peace out
    }
}
