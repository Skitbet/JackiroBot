package gay.skitbet.jackiro

import gay.skitbet.jackiro.database.ServerConfigRepository
import gay.skitbet.jackiro.listener.MainListener
import gay.skitbet.jackiro.managers.CommandHandler
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.music.JackiroMusicManager
import gay.skitbet.jackiro.task.LoadGuildsTask
import gay.skitbet.jackiro.task.UpdateStatusTask
import gay.skitbet.jackiro.utils.JackiroConfig
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class Jackiro private constructor(config: JackiroConfig) {

    companion object {
        val random: Random = Random()
        val logger = LoggerFactory.getLogger(Jackiro::class.java)

        lateinit var instance: Jackiro
        lateinit var config: JackiroConfig
            private set

        fun getInstance(): Jackiro =
            instance ?: throw IllegalStateException("Jackiro has not been initialized yet.")

        fun initialize(config: JackiroConfig) {
            if (instance != null) {
                throw IllegalStateException("Jackiro has already been initialized.")
            }
            Jackiro(config).start()
        }
    }


    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(8)
    val startTime: Instant = Instant.now()
    var readyShards: Int = 0

    lateinit var jackiroMusicManager: JackiroMusicManager
        private set
    lateinit var commandHandler: CommandHandler
        private set
    lateinit var shardManager: ShardManager
        private set

    init {
        Companion.config = config
        instance = this
    }

    private fun start() {
        try {
            initializeMongoDB()
            initializeShardManager()
            commandHandler = CommandHandler(shardManager!!)
            logger.info("Jackiro bot has started successfully!")
        } catch (e: Exception) {
            logger.error("Failed to start Jackiro: ", e)
            shutdown()
        }
    }

    private fun initializeMongoDB() {
        logger.info("Connecting to MongoDB...")
        MongoManager.connect("jackiro")
        logger.info("MongoDB connected.")
    }

    private fun initializeShardManager() {
        logger.info("Initializing Shard Manager...")
        val builder = DefaultShardManagerBuilder.createDefault(config!!.clientToken)
            .setShardsTotal(config!!.shardCount)
            .enableIntents(
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
            )
            .setActivity(Activity.listening("beep boop .. loading"))
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .addEventListeners(MainListener())

        shardManager = builder.build()
    }

    fun onReady() {
        UpdateStatusTask().start(executor)
        LoadGuildsTask(this).start(executor)
        commandHandler?.registerCommands()
        jackiroMusicManager = JackiroMusicManager()
    }

    fun shutdown() {
        logger.warn("Shutting down Jackiro...")
        shardManager?.shutdown()
        executor.shutdownNow()
        System.exit(1)
    }

}
