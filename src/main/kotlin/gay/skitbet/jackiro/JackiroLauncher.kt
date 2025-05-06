package gay.skitbet.jackiro

import com.google.gson.GsonBuilder
import gay.skitbet.jackiro.utils.JackiroConfig
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets

object JackiroLauncher {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile = File("./config.json")

    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadOrCreateConfig()
        Jackiro.initialize(config)
    }

    /**
     * Loads or creates a configuration for the bot!
     */
    private fun loadOrCreateConfig(): JackiroConfig {
        return if (configFile.exists()) {
            try {
                val json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8 as String?)
                gson.fromJson(json, JackiroConfig::class.java)
            } catch (e: Exception) {
                System.err.println("Failed to read config file: ${e.message}")
                e.printStackTrace()
                System.exit(1)
                throw RuntimeException("Unreachable") // for compiler
            }
        } else {
            createDefaultConfig()
            println("\nConfig created! Please configure the `config.json` before restarting the bot.")
            System.exit(1)
            throw RuntimeException("Unreachable") // for compiler
        }
    }

    /**
     * Creates default config for the bot
     */
    private fun createDefaultConfig() {
        println("Jackiro Bot - First time setup")
        println("Creating a default config.json...")

        val defaultConfig = JackiroConfig(
            clientSecret = "client secret here",
            clientID = "client ID here",
            clientToken = "bot token here",
            ownerId = "Discord user ID here",
            currentlyPlaying = mutableListOf("Life")
        )

        try {
            FileUtils.writeStringToFile(configFile, gson.toJson(defaultConfig), StandardCharsets.UTF_8 as String?)
        } catch (e: Exception) {
            System.err.println("Failed to create default config: ${e.message}")
            e.printStackTrace()
        }
    }
}
