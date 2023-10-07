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

/**
 * Service class for processing elements adding
 *
 * @author enovak89
 */
@Service
public class AddElementCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(AddElementCommand.class);

    /**
     * String builder contains result message text
     */
    private static final StringBuilder messageText = new StringBuilder();

    private static CategoryService categoryService;

    private AddElementCommand(CategoryService categoryService) {
        super("/addElement", "Add element to categories tree");
        AddElementCommand.categoryService = categoryService;
    }

    public static IBotCommand getAddElementCommand() {
        return new AddElementCommand(categoryService);
    }

    /**
     * Method to add elements to BD
     *
     * @param absSender
     * @param user      - user from message
     * @param chat      - chat from message
     * @param arguments - parameters of addElement command
     * @throws IllegalArgumentException
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        //adding root element
        if (arguments.length == 1) {

            try {
                categoryService.addRootElement(arguments[0].toLowerCase());
                messageText.replace(0, messageText.length(), "The root element \"" + arguments[0]
                        + "\" was added successfully");
                addCommandResultAnswer(absSender, chat, messageText.toString());
            } catch (IllegalArgumentException e) {
                messageText.replace(0, messageText.length(), e.getMessage());
                addCommandResultAnswer(absSender, chat, messageText.toString());
                logger.error(messageText.toString());
            }
            //adding child element
        } else if (arguments.length == 2) {

            try {
                categoryService.addChildElement(arguments[0].toLowerCase(), arguments[1].toLowerCase());
                messageText.replace(0, messageText.length(), "The child element \"" + arguments[1]
                        + "\" was added successfully");
                addCommandResultAnswer(absSender, chat, messageText.toString());
            } catch (IllegalArgumentException e) {
                messageText.replace(0, messageText.length(), e.getMessage());
                addCommandResultAnswer(absSender, chat, messageText.toString());
                logger.error(messageText.toString());
            }
            //forming answer message when not required parameters number
        } else {
            messageText.replace(0, messageText.length(), "The command /addElement requires one or two parameters");
            addCommandResultAnswer(absSender, chat, messageText.toString());
            logger.error(messageText.toString());
        }
    }

    /**
     * Method to send message with addCommand result text
     *
     * @param absSender
     * @param chat      - chat from message
     * @param text      - message text to send
     * @throws TelegramApiException
     */
    private void addCommandResultAnswer(AbsSender absSender, Chat chat, String text) {

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
