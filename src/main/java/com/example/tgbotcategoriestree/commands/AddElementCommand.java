package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.services.CategoryService;
import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class AddElementCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(AddElementCommand.class);

    private static StringBuilder messageText = new StringBuilder();

    private static CategoryService categoryService;

    private AddElementCommand(CategoryService categoryService) {
        super("/addElement", "Add element to categories tree");
        AddElementCommand.categoryService = categoryService;
    }

    public static IBotCommand getAddElementCommand() {
        return new AddElementCommand(categoryService);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        if (arguments.length == 1) {
            try {
                categoryService.addRootElement(arguments[0].toLowerCase());
                messageText.replace(0, messageText.length(), "Element \"" + arguments[0]
                        + "\" was added successfully");
                addCommandResultAnswer(absSender, chat, messageText.toString());
            } catch (IllegalArgumentException e) {
                messageText.replace(0, messageText.length(), "Sorry, but element \"" + arguments[0]
                        + "\" has already been added before");
                addCommandResultAnswer(absSender, chat, messageText.toString());
                logger.error(e.getMessage());
            }
        } else if (arguments.length == 2) {
            System.out.println(arguments[0]);
            System.out.println(arguments[1]);
        } else {
            messageText.replace(0, messageText.length(),"The command /addElement requires one or two parameters");
            addCommandResultAnswer(absSender, chat, messageText.toString());
            throw new IllegalArgumentException(messageText.toString());
        }
    }

    public void addCommandResultAnswer(AbsSender absSender, Chat chat, String text) {
        SendMessage addCommandResultMessage = new SendMessage();
        addCommandResultMessage.setChatId(chat.getId());
        addCommandResultMessage.setText(text);
        try {
            absSender.execute(addCommandResultMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

}
