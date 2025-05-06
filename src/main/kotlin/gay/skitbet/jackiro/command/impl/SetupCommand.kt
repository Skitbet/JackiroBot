package gay.skitbet.jackiro.command.impl

import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import gay.skitbet.jackiro.managers.SetupManager.getSetup
import gay.skitbet.jackiro.managers.SetupManager.startSetup
import gay.skitbet.jackiro.utils.JackiroEmbed
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.util.List

class SetupCommand : Command("setup", "Run the quick setup on Jackiro for this Guild!", Permission.ADMINISTRATOR, null, true) {
    override fun execute(context: CommandContext) {
        val session = getSetup(context.guild!!.getId())
        if (session != null) {
            context.reply(JackiroEmbed().error("A setup is already in progress!"))
            return
        }

        startSetup(context)
    }

    override fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?> {
        return mutableListOf()
    }
}
