package com.example.tgbotcategoriestree.updatesHandlers;

import com.example.tgbotcategoriestree.BotConfig;
import com.example.tgbotcategoriestree.commands.*;
import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.TelegramLongPollingCommandBotCustom;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@NoArgsConstructor(force = true)
public class UpdatesHandler extends TelegramLongPollingCommandBotCustom {
    private final Logger logger = LoggerFactory.getLogger(UpdatesHandler.class);

    public UpdatesHandler(String botToken) {
        super(botToken);

        register(new HelloCommand());
        register(AddElementCommand.getAddElementCommand());
        register(ViewTreeCommand.getViewTreeCommand());
        register(RemoveElementCommand.getRemoveElementCommand());
        register(DownloadCommand.getDownloadCommand());
        UploadCommand uploadCommand = new UploadCommand();
        register(uploadCommand);
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);

        registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId());
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help ");
            try {
                absSender.execute(commandUnknownMessage);
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }

    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (checkMessageHasDocAndCorrectCaption(message)) {
                System.out.println("doc");
            }

            if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId());
                echoMessage.setText("Hey here's your message:\n" + message.getText());

                try {
                    execute(echoMessage);
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private boolean checkMessageHasDocAndCorrectCaption(Message message) {
        try {
            boolean result = message.hasDocument() && message.getCaption().equals(UploadCommand.COMMAND_IDENTIFIER);
        } catch (NullPointerException e) {
            SendMessage incorrectCaptureMessage = new SendMessage();
            incorrectCaptureMessage.setChatId(message.getChatId());
            incorrectCaptureMessage.setText("Caption is incorrect, please write " + UploadCommand.COMMAND_IDENTIFIER
                    + " when send the file");

            try {
                execute(incorrectCaptureMessage);
            } catch (TelegramApiException exception) {
                logger.error(exception.getMessage());
            }
            return;
        }
    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }
}