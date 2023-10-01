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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ViewTreeCommand extends BotCommandCustom {

    private final Logger logger = LoggerFactory.getLogger(ViewTreeCommand.class);

    private static StringBuilder messageText = new StringBuilder();

    private static Map<String, List<String>> categoriesTree = new TreeMap<>();

    private static CategoryService categoryService;

    private ViewTreeCommand(CategoryService categoryService) {
        super("/viewTree", "View the entire categories tree");
        ViewTreeCommand.categoryService = categoryService;
    }

    public static IBotCommand getViewTreeCommand() {
        return new ViewTreeCommand(categoryService);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        categoriesTree.put("one", List.of("two", "three"));
        categoriesTree.put("four", List.of("five"));

        StringBuilder viewMessageBuilder = new StringBuilder("<b>The categories tree:</b>\n\n");

        categoriesTree.forEach((key, value) -> {
            viewMessageBuilder.append(key).append("\n");
            value
                    .forEach(child -> viewMessageBuilder.append("  -").append(child).append("\n"));
        });


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
