package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.repository.CategoryRepository;
import com.example.tgbotcategoriestree.services.CategoryService;
import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class AddElementCommand extends BotCommandCustom {

    private final CategoryService categoryService;
    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public AddElementCommand() {
        super("/addElement", "Add element to categories tree");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        if (arguments.length == 1) {
            System.out.println(arguments[0]);
            categoryService.addRootElement(arguments[0]);
        } else if (arguments.length == 2) {
            System.out.println(arguments[0]);
            System.out.println(arguments[1]);
        } else {
            incorrectCommandParametersNumbersAnswer(absSender, chat);
            throw new IllegalArgumentException("The command /addElement requires one or two parameters");
        }
    }

    public void incorrectCommandParametersNumbersAnswer(AbsSender absSender, Chat chat) {
        SendMessage incorrectCommandParametersNumbersMessage = new SendMessage();
        incorrectCommandParametersNumbersMessage.setChatId(chat.getId());
        incorrectCommandParametersNumbersMessage.setText("The command /addElement requires one or two parameters");
        try {
            absSender.execute(incorrectCommandParametersNumbersMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

}
