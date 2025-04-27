package gay.skitbet.jackiro.command;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@Setter
public class CommandContext {

    private final SlashCommandInteractionEvent event;

    private final User user;
    private final Member member;
    private final Guild guild;
    private final MessageChannelUnion channel;

    public CommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
        this.user = event.getUser();
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.channel = event.getChannel();
    }

    public void reply(String message) {
        event.reply(message).queue();
    }

    public void reply(MessageEmbed embed, MessageEmbed... embeds) {
        event.replyEmbeds(embed, embeds).queue();
    }

    public OptionMapping getOption(String key) {
        return event.getOption(key);
    }
}
