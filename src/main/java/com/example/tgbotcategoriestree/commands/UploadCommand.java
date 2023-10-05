package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class UploadCommand extends BotCommandCustom {
    public final static boolean UPLOAD_PERMISSION = false;

    private final Logger logger = LoggerFactory.getLogger(UploadCommand.class);

    public UploadCommand() {
        super("/upload", "Upload excel file containing the entire categories tree to save it in BD");
    }

    public void processMessageWithDocument(Message message) {
        System.out.println("doc ok");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        execute(absSender, message.getFrom(), message.getChat(), arguments);
        System.out.println("doc ok");
    }

    private boolean checkMessageHasDocAndCorrectCaption(Message message) {
        try {
            boolean result = message.hasDocument() && message.getCaption().equals(this.getCommandIdentifier());
        } catch (NullPointerException e) {
            SendMessage incorrectCaptureMessage = new SendMessage();
            incorrectCaptureMessage.setChatId(message.getChatId());
            incorrectCaptureMessage.setText("Caption is incorrect, please write " + this.getCommandIdentifier()
                    + " when send the file");

//            try {
//                execute(incorrectCaptureMessage);
//            } catch (TelegramApiException exception) {
//                logger.error(exception.getMessage());
//            }
        }
        return true;
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage getUploadCommandMessage = new SendMessage();
        getUploadCommandMessage.setChatId(chat.getId());
        getUploadCommandMessage.setText("Please, in your response message send XLSX file," +
                " where put root categories in the first column, its child categories - in the next columns. " +
                "\nIn message caption write " + this.getCommandIdentifier());

        try {
            absSender.execute(getUploadCommandMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }
}
