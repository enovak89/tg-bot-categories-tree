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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service class to get user the entire categories tree
 *
 * @author enovak89
 */
@Service
public class ViewTreeCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(ViewTreeCommand.class);

    private static final Map<String, List<String>> categoriesTree = new TreeMap<>();

    private static CategoryService categoryService;

    private ViewTreeCommand(CategoryService categoryService) {
        super("/viewTree", "View the entire categories tree");
        ViewTreeCommand.categoryService = categoryService;
    }

    public static IBotCommand getViewTreeCommand() {
        return new ViewTreeCommand(categoryService);
    }

    /**
     * Method to form and send message text with categories tree
     *
     * @param absSender
     * @param user      - user from message
     * @param chat      - chat from message
     * @param arguments - parameters of command
     * @throws TelegramApiException
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

//        categoriesTree.clear();
//        categoriesTree.putAll(categoryService.viewCategoriesTree());

        StringBuilder viewMessageBuilder = new StringBuilder("<b>The categories tree:</b>\n");
        viewMessageBuilder.append(categoryService.viewCategoriesTreeString());


//        categoriesTree.forEach((key, value) -> {
//            viewMessageBuilder.append(key).append("\n");
//            value
//                    .forEach(child -> viewMessageBuilder.append("  -").append(child).append("\n"));
//        });

        SendMessage viewTreeMessage = new SendMessage();
        viewTreeMessage.setChatId(chat.getId().toString());
        viewTreeMessage.enableHtml(true);
        viewTreeMessage.setText(viewMessageBuilder.toString());

        try {
            absSender.execute(viewTreeMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

}
