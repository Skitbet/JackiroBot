package gay.skitbet.jackiro.command.impl

import gay.skitbet.jackiro.command.Command
import gay.skitbet.jackiro.command.CommandContext
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class PingCommand : Command("ping", "PONG PONG PONG", null, true) {
    override fun execute(context: CommandContext) {
    }

    override fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?> {
        return options
    }

}
