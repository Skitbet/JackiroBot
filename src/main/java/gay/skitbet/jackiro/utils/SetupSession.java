package gay.skitbet.jackiro.utils;

import gay.skitbet.jackiro.Jackiro;
import gay.skitbet.jackiro.command.CommandContext;
import gay.skitbet.jackiro.managers.CommandHandler;
import gay.skitbet.jackiro.managers.MongoManager;
import gay.skitbet.jackiro.managers.SetupManager;
import gay.skitbet.jackiro.model.ServerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

// TODO: Turn the setup into a multi-class setup state system.

@Getter
public class SetupSession {

    @AllArgsConstructor
    @Getter
    public enum SetupStep {
        NICKNAME("🤔 What nickname should the bot use? (Type `none` for no nickname)"),
        UPDATE_CHANNEL("📢 Please mention the channel where bot updates should be sent. (Type `none` to skip)"),
        DISABLED_COMMANDS("🚫 Choose which commands you'd like to disable."),
        XP_ENABLED("🎯 Would you like to enable the XP system? (yes/no)"),
        XP_MULTIPLIER("📈 Set the XP multiplier (e.g. `1.0` for normal gain, `2.0` for double XP). ");

        private final String message;

        private SetupStep nextStep() {
            int index = getStepIndex(this);
            if (index + 1 >= values().length) {
                return null;
            }
            return values()[index + 1];
        }

        public static int getStepIndex(SetupStep step) {
            int index = 0;
            for (SetupStep value : values()) {
                if (value == step) {
                    return index;
                }
                index++;
            }
            return index;
        }
    }

    private final Member member;
    private final TextChannel channel;
    private final ServerConfig config;
    private SetupStep currentStep;
    private Message questionMessage;

    public SetupSession(CommandContext context) {
        this.member = context.getMember();
        this.channel = context.getChannel().asTextChannel();
        this.config = MongoManager.getServerConfigRepository().findById(context.getGuild().getId());

        startSession();
    }

    private void startSession() {
        this.currentStep = SetupStep.NICKNAME;
        channel.sendMessage("⏳ Setting up, please wait...").queue(msg -> {
            this.questionMessage = msg;
            askNextQuestion();
        });
    }

    public void handleMessage(Message message) {
        String content = message.getContentRaw().trim();
        message.delete().queue();

        switch (currentStep) {
            case NICKNAME -> handleNicknameStep(content);
            case UPDATE_CHANNEL -> handleUpdateChannelStep(message);
            case XP_ENABLED -> handleXpEnabledStep(content);
            case XP_MULTIPLIER -> handleXpMultiplierStep(content);
            default -> { /* Should never happen */ }
        }

        currentStep = currentStep != null ? currentStep.nextStep() : null;
        if (isFinished()) {
            finish();
        } else {
            askNextQuestion();
        }
    }

    private void handleNicknameStep(String content) {
        if (!content.equalsIgnoreCase("none")) {
            channel.getGuild().getSelfMember().modifyNickname(content).queue();
        }
    }

    private void handleUpdateChannelStep(Message message) {
        String content = message.getContentRaw().trim();
        if (!content.equalsIgnoreCase("none") && !message.getMentions().getChannels().isEmpty()) {
            String channelId = message.getMentions().getChannels().get(0).getId();
            config.botUpdateChannelId = channelId;
        }
    }

    private void handleXpEnabledStep(String content) {
        boolean enabled = content.equalsIgnoreCase("yes") || content.equalsIgnoreCase("true");
        config.xpEnabled = enabled;
    }

    private void handleXpMultiplierStep(String content) {
        try {
            double multiplier = Double.parseDouble(content);
            config.xpMultiplier = multiplier;
        } catch (NumberFormatException e) {
            config.xpMultiplier = 1.0;
            channel.sendMessage("❌ Invalid number. Defaulting to 1.0").queue();
        }
    }

    private void askNextQuestion() {
        if (SetupStep.getStepIndex(currentStep) < SetupStep.values().length) {
            String question = currentStep.getMessage();
            editQuestion(question);

            if (currentStep == SetupStep.DISABLED_COMMANDS) {
                sendCommandDropdown();
            }
        }
    }

    private void editQuestion(String content) {
        if (questionMessage != null) {
            questionMessage.editMessage(content).setComponents().queue(updated -> questionMessage = updated);
        }
    }

    private boolean isFinished() {
        return currentStep == null;
    }

    private void finish() {
        MongoManager.getServerConfigRepository().save(config);
        SetupManager.endSetup(this);

        if (questionMessage != null) {
            questionMessage.editMessage("✅ Setup complete! Thank you for setting up **Jackiro**. You can now use the bot as intended!")
                    .setEmbeds()
                    .queue();
        }
    }

    public void sendCommandDropdown() {
        CommandHandler commandHandler = Jackiro.getInstance().getCommandHandler();
        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("setup_command_select")
                .setPlaceholder("Select commands to disable")
                .setMinValues(1)
                .setMaxValues(Math.max(1, commandHandler.getCommands().size())); // at least 1

        commandHandler.getCommands().forEach((name, command) ->
                menuBuilder.addOption(name, name)
        );

        if (questionMessage != null) {
            questionMessage.editMessageEmbeds(
                            new JackiroEmbed()
                                    .setDescription("🚫 Please select the commands you would like to disable.")
                                    .build())
                    .setActionRow(menuBuilder.build())
                    .queue(updated -> questionMessage = updated);
        }
    }

    public void handleDisableCommands(@Unmodifiable List<SelectOption> selectedOptions) {
        config.disabledCommands.clear();
        for (SelectOption option : selectedOptions) {
            config.disabledCommands.add(option.getValue());
        }

        MongoManager.getServerConfigRepository().save(config);
        Jackiro.getInstance().getCommandHandler().registerGuildCommands(member.getGuild());

        currentStep = currentStep != null ? currentStep.nextStep() : null;
        if (isFinished()) {
            finish();
        } else {
            askNextQuestion();
        }
    }
}
