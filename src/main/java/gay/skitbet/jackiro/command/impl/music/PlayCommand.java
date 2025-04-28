package gay.skitbet.jackiro.command.impl.music;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.Command;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.utils.JackiroModule;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayCommand extends Command {
    public PlayCommand() {
        super("play", "Play media by a url!", JackiroModule.MUSIC);
    }

    @Override
    public void execute(CommandContext context) {
        String url = context.getOption("url").getAsString();
        if (url.contains("youtube.com") && !context.getUser().getId().equals(Jackiro.config.getOwnerId())) {
            context.reply("Sorry, youtube music currently doesnt work. Please try again another day! For now you can use soundcloud, bandcamp, and twitch! Will be fixed as soon as possible.");
            return;
        }
        Jackiro.getInstance().getJackiroMusicManager().loadAndPlay(context.getChannel().asTextChannel(), url);
    }

    @Override
    public List<OptionData> addOptions(List<OptionData> options) {
        options.add(new OptionData(OptionType.STRING, "url", "Url of media to play", true));
        return options;
    }
}
