package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.services.FileService;
import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;

@Service
public class DownloadCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(DownloadCommand.class);

    private static FileService fileService;

    private DownloadCommand(FileService fileService) {
        super("/download", "Download excel file containing the entire categories tree");
        DownloadCommand.fileService = fileService;
    }

    public static IBotCommand getDownloadCommand() {
        return new DownloadCommand(fileService);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendDocument document = new SendDocument();
        document.setChatId(chat.getId().toString());

        try {
            document.setDocument(new InputFile(fileService.createWorkBook(), fileService.getFileName()));
            absSender.execute(document);
        } catch (TelegramApiException | FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }
}
