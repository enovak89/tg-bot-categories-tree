package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class UploadCommand extends BotCommandCustom {
    public final static String COMMAND_IDENTIFIER = "/upload";

    private final Logger logger = LoggerFactory.getLogger(UploadCommand.class);

    public UploadCommand() {
        super(COMMAND_IDENTIFIER, "Upload excel file containing the entire categories tree to save it in BD");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage getUploadCommandMessage = new SendMessage();
        getUploadCommandMessage.setChatId(chat.getId());
        getUploadCommandMessage.setText("Please, in your response message send XLSX file," +
                " where put root categories in the first column, its child categories - in the next columns. " +
                "\nIn message caption write " + COMMAND_IDENTIFIER);

        try {
            absSender.execute(getUploadCommandMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }
}
