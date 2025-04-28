package gay.skitbet.jackiro.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.utils.JackiroEmbed;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JackiroMusicManager extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers = new HashMap<>();

    public JackiroMusicManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        Jackiro.getInstance().getShardManager().addEventListener(this);
    }

    public synchronized GuildMusicManager getGuildAndPlayer(Guild guild, TextChannel musicChannel) {
        GuildMusicManager musicManager = musicManagers.get(guild.getId());

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, musicChannel);
            musicManagers.put(guild.getId(), musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(CommandContext context, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAndPlayer(context.getGuild(), context.getChannel().asTextChannel());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                // Prepare the track info for the embed message
                String duration = formatDuration(audioTrack.getDuration());
                String nextTrack = musicManager.scheduler.peekNextTrack() != null ?
                        musicManager.scheduler.peekNextTrack().getInfo().title : "No track in queue.";

                // Send the track info embed
                context.reply(new JackiroEmbed()
                        .setColor(Color.GREEN)
                        .setTitle("Track Added")
                        .setDescription("**" + audioTrack.getInfo().title + "** has been added to the queue!")
                        .addField("Duration", duration, true)
                        .addField("Next Track", nextTrack, true)
                        .setThumbnailUrl(audioTrack.getInfo().uri)
                        .build());

                play(context, context.getGuild(), musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                String duration = formatDuration(firstTrack.getDuration());
                String nextTrack = musicManager.scheduler.peekNextTrack() != null ?
                        musicManager.scheduler.peekNextTrack().getInfo().title : "No track in queue.";

                // Send playlist info
                context.reply(new JackiroEmbed()
                        .setColor(Color.GREEN)
                        .setTitle("Playlist Loaded")
                        .setDescription("Added **" + firstTrack.getInfo().title + "** from playlist **" + audioPlaylist.getName() + "** to the queue.")
                        .addField("Duration", duration, true)
                        .addField("Next Track", nextTrack, true)
                        .setThumbnailUrl(firstTrack.getInfo().uri)
                        .build());

                play(context, context.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                context.reply(new JackiroEmbed()
                        .setColor(Color.RED)
                        .setTitle("No Results Found")
                        .setDescription("Couldn't find anything for **" + trackUrl + "**. Please check the URL and try again.")
                        .build());
            }

            @Override
            public void loadFailed(FriendlyException e) {
                String errorMessage = "Could not load track: " + e.getMessage();
                context.reply(new JackiroEmbed()
                        .setColor(Color.RED)
                        .setTitle("Load Failed")
                        .setDescription(errorMessage)
                        .build());
                e.printStackTrace();
            }
        });
    }

    private void play(CommandContext context, Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        if (musicManager.player.getPlayingTrack() != null) {
            int position = musicManager.scheduler.getTrackPositionInQueue(track);
            String nextTrack = musicManager.scheduler.peekNextTrack() != null ?
                    musicManager.scheduler.peekNextTrack().getInfo().title : "No track in queue.";

            TextChannel musicChannel = musicManager.musicChannel;
            if (musicChannel == null) {
                musicChannel = guild.getDefaultChannel().asTextChannel();
            }
            AudioTrackInfo currentTrack = musicManager.player.getPlayingTrack().getInfo();
            musicChannel.sendMessageEmbeds(new JackiroEmbed()
                    .setColor(Color.YELLOW)
                    .setTitle("Track Added to Queue")
                    .setDescription("The track **" + currentTrack.title + "** is already playing! It's now in the queue.")
                    .addField("Position in Queue", String.valueOf(position), true)
                    .addField("Next Track", nextTrack, true)
                    .build()).queue();
        }

        connectToVoiceChannel(context.getMember().getVoiceState().getChannel().asVoiceChannel(), guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    private void connectToVoiceChannel(VoiceChannel voiceChannel, AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    public static String formatDuration(long durationMillis) {
        long minutes = durationMillis / 60000;
        long seconds = (durationMillis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
