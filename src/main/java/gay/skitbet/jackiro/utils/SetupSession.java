package gay.skitbet.jackiro.utils;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.database.ServerConfigRepository;
import gay.skitbet.jackiro.managers.SetupManager;
import gay.skitbet.jackiro.model.ServerConfig;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

@Data
public class SetupSession {

    private final Member member;
    private final TextChannel channel;
    private final ServerConfig config;
    private int currentStep = 0;

    private Message questionMessage;

    private static List<String> QUESTIONS = List.of(
            "What would you like the bot nickname to be? (Type \"none\" for no nickname)"
    );

    public SetupSession(CommandContext context) {
        this.member = context.getMember();
        this.channel = context.getChannel().asTextChannel();
        this.config = Jackiro.getInstance().getServerConfigRepository().load(channel.getGuild().getId());


         context.getEvent().reply("🤔 Loading the setup...").queue(hook -> {
             questionMessage = hook.getCallbackResponse().getMessage();
             start();
         });
    }

    public void start() {
        askNextQuestion();
    }

    public void handleMessage(Message message) {
        System.out.println(message);
        String messageRaw = message.getContentRaw();
        message.delete().queue();

        switch (currentStep) {
            case 0:
                if (!messageRaw.equalsIgnoreCase("none")) {
                    message.getGuild().getSelfMember().modifyNickname(messageRaw).queue();
                    updateQuestion("Jackiro nickname has been updated to \"" + messageRaw + "\"!");
                    break;
                }
                updateQuestion("No nickname has been set.");
                break;
        }

        currentStep++;
        if (isFinished()) {
            finish();
        } else {
            askNextQuestion();
        }
    }

    private void updateQuestion(String message) {
        questionMessage.editMessage(message).queue(newMsg -> {
            questionMessage = newMsg;
        });
    }

    private void askNextQuestion() {
        updateQuestion(QUESTIONS.get(currentStep));
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
