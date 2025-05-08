package gay.skitbet.jackiro.utils

import gay.skitbet.jackiro.Jackiro
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.LocalDateTime

class JackiroEmbed {
    var title: String? = null
    var description: String? = null
    var footerText: String? = null
    var imageUrl: String? = null
    var thumbnailUrl: String? = null
    var color: Color? = Color.BLUE
    var authorName: String? = null
    var authorUrl: String? = null
    var authorIconUrl: String? = null
    private val fields: MutableList<MessageEmbed.Field> = mutableListOf()

    fun setTitle(value: String?): JackiroEmbed = apply { title = value }
    fun setDescription(value: String?): JackiroEmbed = apply { description = value }
    fun setFooterText(value: String?): JackiroEmbed = apply { footerText = value }
    fun setImageUrl(value: String?): JackiroEmbed = apply { imageUrl = value }
    fun setThumbnailUrl(value: String?): JackiroEmbed = apply { thumbnailUrl = value }
    fun setColor(value: Color?): JackiroEmbed = apply { color = value }
    fun setAuthorName(value: String?): JackiroEmbed = apply { authorName = value }
    fun setAuthorUrl(value: String?): JackiroEmbed = apply { authorUrl = value }
    fun setAuthorIconUrl(value: String?): JackiroEmbed = apply { authorIconUrl = value }

    fun addField(name: String, value: String, inLine: Boolean): JackiroEmbed = apply {
        fields.add(MessageEmbed.Field(name, value, inLine))
    }

    fun build(): MessageEmbed {
        val embed = createEmbedBasics()

        title?.let { embed.setTitle(it) }
        description?.let { embed.setDescription(it) }
        footerText?.let { embed.setFooter(it) }
        imageUrl?.let { embed.setImage(it) }
        thumbnailUrl?.let { embed.setThumbnail(it) }
        color?.let { embed.setColor(it) }

        if (authorName != null) {
            embed.setAuthor(authorName, authorUrl, authorIconUrl)
        }

        fields.forEach { embed.addField(it) }

        return embed.build()
    }

    fun error(message: String): MessageEmbed {
        return EmbedBuilder()
            .setTimestamp(LocalDateTime.now())
            .setColor(Color.RED)
            .setFooter("Jackiro", ICON_URL)
            .setTitle("Uh oh!")
            .setDescription("❌ An error has occurred! \n$message")
            .build()
    }

    private fun createEmbedBasics(): EmbedBuilder {
        return EmbedBuilder().apply {
            setTimestamp(LocalDateTime.now())
            setFooter("Jackiro", ICON_URL)
        }
    }

    companion object {
        private val ICON_URL: String? = Jackiro.instance
            .shardManager
            .shards
            .firstOrNull()
            ?.selfUser
            ?.avatarUrl

        fun getNewGuildEmbed(guild: Guild): MessageEmbed {
            return JackiroEmbed()
                .setColor(Color.CYAN)
                .setTitle("Hello, I'm Jackiro!")
                .setDescription("Thanks for adding me to your server! I'm a general-purpose Discord bot that helps keep things running smoothly!")
                .addField("Getting Started", "To get started you can use \"/setup\" to configure me!", false)
                .addField("Features", "• Automoderation\n• Custom Commands\n• Server Utilities\n• And much more!", false)
                .setThumbnailUrl(ICON_URL)
                .build()
        }
    }
}
