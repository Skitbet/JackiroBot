package gay.skitbet.jackiro.command

import gay.skitbet.jackiro.utils.JackiroModule
import lombok.Data
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.util.*

@Data
abstract class Command {
    val name: String
    val description: String
    val module: JackiroModule?
    val ephemeral: Boolean

    var permission: Permission? = null

    constructor(name: String, description: String, module: JackiroModule?, ephemeral: Boolean) {
        this.name = name.lowercase(Locale.getDefault())
        this.description = description
        this.module = module
        this.ephemeral = ephemeral
    }

    constructor(
        name: String,
        description: String,
        permission: Permission?,
        module: JackiroModule?,
        ephemeral: Boolean
    ) {
        this.name = name.lowercase(Locale.getDefault())
        this.description = description
        this.permission = permission
        this.module = module
        this.ephemeral = ephemeral
    }

    abstract fun execute(context: CommandContext)
    abstract fun addOptions(options: MutableList<OptionData?>): MutableList<OptionData?>

    fun toData(): CommandData {
        val options = addOptions(ArrayList<OptionData?>())

        return CommandDataImpl(name, description)
            .addOptions(options)
    }
}
