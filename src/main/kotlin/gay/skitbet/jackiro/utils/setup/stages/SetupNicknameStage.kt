package gay.skitbet.jackiro.utils.setup.stages

import gay.skitbet.jackiro.utils.setup.SetupSession
import gay.skitbet.jackiro.utils.setup.SetupStage
import net.dv8tion.jda.api.entities.Message

class SetupNicknameStage : SetupStage("🤔 What nickname should the bot use? (Type `none` for no nickname)") {

    override fun handleMessageReceived(message: Message): Boolean {
        val contentRaw = message.contentRaw
        if (!contentRaw.equals("none", ignoreCase = true)) {
            setupSession.channel.guild.selfMember.modifyNickname(contentRaw).queue()
        }
        return true
    }
}
