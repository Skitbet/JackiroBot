package gay.skitbet.jackiro.command.impl.utilities

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.utils.JackiroEmbed
import gay.skitbet.jackiro.utils.JackiroModule
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.time.Duration
import java.time.Instant
import java.util.List

class InfoCommand : Command("info", "Shows the current bot status and information!", JackiroModule.UTILITIES, false) {
     override fun execute(context: CommandContext) {
        val embed = JackiroEmbed()
        embed.setTitle("✅ Jackiro Information & Status")
        embed.setDescription("Jackiro is a general purpose discord bot to help run things smoothly and fun! Manage everything from within Discord without the need of a panel! (Though one will be added soon)")
        embed.addField("🛠 Uptime:", this.uptime, true)
        embed.addField(
            "🦊 Members:",
            java.lang.String.valueOf(Jackiro.instance.shardManager.users.size + 1),
            true
        )
        embed.addField(
            "🎉 Guilds:",
            java.lang.String.valueOf(Jackiro.instance.shardManager.guilds.size),
            true
        )
        embed.addField("💕 Invite:", "Coming soon", true)
        context.reply(embed.build())
    }

    private val uptime: String
        get() {
            val uptime =
                Duration.between(Jackiro.instance.startTime, Instant.now())

            val days = uptime.toDays()
            val hours = uptime.toHours() % 24
            val minutes = uptime.toMinutes() % 60
            val seconds = uptime.getSeconds() % 60

            return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds)
        }

    override fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?> {
        return mutableListOf()
    }
}
