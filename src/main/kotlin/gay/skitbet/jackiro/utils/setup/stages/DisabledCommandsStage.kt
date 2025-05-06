package gay.skitbet.jackiro.utils.setup.stages

import gay.skitbet.jackiro.Jackiro
import gay.skitbet.jackiro.managers.CommandHandler
import gay.skitbet.jackiro.managers.MongoManager
import gay.skitbet.jackiro.utils.setup.SetupStage
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class DisabledCommandsStage : SetupStage("🚫 Choose which commands you'd like to disable.") {

    override fun getActionRow(): ItemComponent {
        val commandHandler = Jackiro.instance.commandHandler
        val menuBuilder = StringSelectMenu.create("jackiro_setup")
            .setPlaceholder("Select commands to disable")
            .setMinValues(1)
            .setMaxValues(maxOf(1, commandHandler.commands.size))

        commandHandler.commands.forEach { (name, command) ->
            menuBuilder.addOption(name, name)
        }

        return menuBuilder.build()
    }

    override fun handleActionRow(event: StringSelectInteractionEvent): Boolean {
        setupSession.config.disabledCommands.clear()
        for (selectedOption in event.selectedOptions) {
            setupSession.config.disabledCommands.add(selectedOption.value)
        }

        MongoManager.serverConfigRepository.save(setupSession.config)
        Jackiro.instance.commandHandler.registerGuildCommands(setupSession.member.guild)
        return true
    }

    override fun handleMessageReceived(message: Message): Boolean {
        return false
    }
}
