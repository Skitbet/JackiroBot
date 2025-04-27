package gay.skitbet.jackiro.utils;

import gay.skitbet.jackiro.Jackiro;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@NoArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class JackiroEmbed {
    private static final String ICON_URL = Jackiro.getInstance().getShardManager().getShards().get(0).getSelfUser().getAvatarUrl();

    public static MessageEmbed getNewGuildEmbed(Guild guild) {
        return new JackiroEmbed()
                .setColor(Color.CYAN)
                .setTitle("Hello, I'm Jackiro!")
                .setDescription("Think your for adding me to your server! I'm a general purpose discord bot that can help keeps things running smootly!")
                .addField("Getting Started", "To get started you can use \"/setup\" to configure me!", false)
                .addField("Features", "• Automoderation\n• Custom Commands\n• Server Utilities\n• And much more!", false)
                .setThumbnailUrl(ICON_URL)
                .build();
    }

    private String title;
    private String description;
    private String footerText;
    private String imageUrl;
    private String thumbnailUrl;
    private Color color = Color.BLUE;
    private String authorName;
    private String authorUrl;
    private String authorIconUrl;
    private List<MessageEmbed.Field> fields = new ArrayList<>();

    /**
     * Builds embed!
     * @return
     */
    public MessageEmbed build() {
        EmbedBuilder embed = createEmbedBasics();

        if (title != null) embed.setTitle(title);
        if (description != null) embed.setDescription(description);
        if (footerText != null) embed.setFooter(footerText);
        if (imageUrl != null) embed.setImage(imageUrl);
        if (thumbnailUrl != null) embed.setThumbnail(thumbnailUrl);
        if (color != null) embed.setColor(color);
        if (authorName != null) embed.setAuthor(authorName, authorUrl, authorIconUrl);
        for (var field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }

    public JackiroEmbed addField(String name, String value, boolean inLine) {
        fields.add(new MessageEmbed.Field(name, value, inLine));
        return this;
    }

    /**
     * creates a basic general error embed
     * @param message
     * @return
     */
    public MessageEmbed error(String message) {
        EmbedBuilder embed = createEmbedBasics();

        embed.setColor(Color.RED);
        embed.setTitle("Uh oh!");
        embed.setDescription("❌ An error has occured! \n" + message);
        return embed.build();
    }

    private EmbedBuilder createEmbedBasics() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTimestamp(LocalDateTime.now());
        embed.setFooter("Jackiro", ICON_URL);

        return embed;
    }

}
