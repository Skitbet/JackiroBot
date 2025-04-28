package gay.skitbet.jackiro.command.impl.music;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import gay.skitbet.jackiro.utils.JackiroModule;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;

public class PlayCommand extends Command {
    public PlayCommand() {
        super("play", "Play media by a url!", JackiroModule.MUSIC, false);
    }

    @Override
    public void execute(CommandContext context) {
        String url = context.getOption("url").getAsString();

        if (!context.getMember().getVoiceState().inAudioChannel() || !(context.getMember().getVoiceState().getChannel() instanceof VoiceChannel)) {
            context.reply(new JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("❌ No Channel!")
                    .setDescription("Sorry, but I cant a channel if your not in one!")
                    .build());
            return;
        }

        if (url.contains("youtube.com") && !context.getUser().getId().equals(Jackiro.config.getOwnerId())) {
            context.reply(new JackiroEmbed()
                    .setColor(Color.RED)
                    .setTitle("❌ YouTube Music Unavailable")
                    .setDescription("Sorry, YouTube music currently doesn't work. Please try again later! For now, you can use platforms like SoundCloud, Bandcamp, and Twitch.")
                    .build());
            return;
        }
        Jackiro.getInstance().getJackiroMusicManager().loadAndPlay(context, url);
    }

    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        options.add(new OptionData(OptionType.STRING, "url", "Url of media to play", true));
        return options;
    }
}
