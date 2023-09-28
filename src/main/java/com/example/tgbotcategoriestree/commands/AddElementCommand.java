package com.example.tgbotcategoriestree.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
public class AddElementCommand extends BotCommand {

    public AddElementCommand() {
        super("addElement", "Add element to categories tree");
    }
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        System.out.println(strings);
    }
}
