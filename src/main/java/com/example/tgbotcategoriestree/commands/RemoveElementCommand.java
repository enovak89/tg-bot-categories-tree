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
public class RemoveElementCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(RemoveElementCommand.class);

    private static StringBuilder messageText = new StringBuilder();

    private static CategoryService categoryService;

    private RemoveElementCommand(CategoryService categoryService) {
        super("/removeElement", "Remove element from categories tree");
        RemoveElementCommand.categoryService = categoryService;
    }

    public static IBotCommand getRemoveElementCommand() {
        return new RemoveElementCommand(categoryService);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        if (arguments.length == 1) {

            try {
                categoryService.removeElement(arguments[0].toLowerCase());
                messageText.replace(0, messageText.length(), "The element \"" + arguments[0]
                        + "\" was removed successfully");
                removeCommandResultAnswer(absSender, chat, messageText.toString());
            } catch (IllegalArgumentException e) {
                messageText.replace(0, messageText.length(), e.getMessage());
                removeCommandResultAnswer(absSender, chat, messageText.toString());
                logger.error(messageText.toString());
            }

        } else {
            messageText.replace(0, messageText.length(), "The command /removeElement requires one parameter");
            removeCommandResultAnswer(absSender, chat, messageText.toString());
            logger.error(messageText.toString());
            categoryService.writeInWorkbookFromDb();
        }
    }

    public void removeCommandResultAnswer(AbsSender absSender, Chat chat, String text) {

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
