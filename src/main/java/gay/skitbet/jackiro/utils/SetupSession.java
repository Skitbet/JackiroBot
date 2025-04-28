package gay.skitbet.jackiro.utils;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.database.ServerConfigRepository;
import gay.skitbet.jackiro.managers.SetupManager;
import gay.skitbet.jackiro.model.ServerConfig;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

@Getter
public class SetupSession {

    private final Member member;
    private final TextChannel channel;
    private final ServerConfig config;
    private int currentStep = 0;
    private Message questionMessage;

    private static final List<String> QUESTIONS = List.of(
            "What would you like the bot nickname to be? (Type \"none\" for no nickname)",
            "Please mention the text channel (#update) that you would like to send bot updates to! (Type \"none\" to have no updates!)"
    );

    public SetupSession(CommandContext context) {
        this.member = context.getMember();
        this.channel = context.getChannel().asTextChannel();
        this.config = Jackiro.getInstance().getServerConfigRepository().load(channel.getGuild().getId());

        context.getEvent().reply("🤔 Loading the setup...").queue(hook -> {
            this.questionMessage = hook.retrieveOriginal().complete();
            start();
        });
    }

    private void start() {
        askNextQuestion();
    }

    public void handleMessage(Message message) {
        String content = message.getContentRaw().trim();
        message.delete().queue();

        switch (currentStep) {
            case 0 -> handleNicknameStep(content);
            case 1 -> handleUpdateChannelStep(message);
            default -> {
                // If unexpected, just continue
            }
        }

        currentStep++;
        if (isFinished()) {
            finish();
        } else {
            askNextQuestion();
        }
    }

    private void handleNicknameStep(String content) {
        if (!content.equalsIgnoreCase("none")) {
            member.modifyNickname(content).queue();
        }
    }

    private void handleUpdateChannelStep(Message message) {
        if (!message.getContentRaw().equalsIgnoreCase("none")) {
            if (!message.getMentions().getChannels().isEmpty()) {
                String channelId = message.getMentions().getChannels().get(0).getId();
                config.botUpdateChannelId = channelId;
            }
        }
    }

    private void askNextQuestion() {
        if (currentStep < QUESTIONS.size()) {
            updateQuestion(QUESTIONS.get(currentStep));
        }
    }

    private void updateQuestion(String content) {
        if (questionMessage != null) {
            questionMessage.editMessage(content).queue(updated -> questionMessage = updated);
        }
    }

    private boolean isFinished() {
        return currentStep >= QUESTIONS.size();
    }

    private void finish() {
        Jackiro.getInstance().getServerConfigRepository().save(config);
        SetupManager.endSetup(this);
        channel.sendMessage("✅ Setup completed successfully!").queue();
    }
}
