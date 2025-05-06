package gay.skitbet.jackiro.command;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.*;
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


    public CommandContext(SlashCommandInteractionEvent event, Command command) {
        this.event = event;
        this.user = event.getUser();
        this.guild = event.getGuild();
        this.member = event.getMember();
        this.channel = event.getChannel();

        this.event.deferReply(command.isEphemeral()).queue();
    }

    public void reply(String message) {
        event.getHook().editOriginal(message).queue();
    }

    public void reply(MessageEmbed embed) {
        event.getHook().editOriginalEmbeds(embed).queue();
    }

    public OptionMapping getOption(String key) {
        return event.getOption(key);
    }
}
