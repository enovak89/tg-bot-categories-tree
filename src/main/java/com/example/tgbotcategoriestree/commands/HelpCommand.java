package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class HelpCommand extends BotCommandCustom {
    private final ICommandRegistry commandRegistry;

    public HelpCommand(ICommandRegistry commandRegistry) {
        super("help", "Get all the commands this bot provides");
        this.commandRegistry = commandRegistry;
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        StringBuilder helpMessageBuilder = new StringBuilder("<b>Help</b>\n");
        helpMessageBuilder.append("These are the registered commands for this Bot:\n\n");

        for (IBotCommand botCommand : commandRegistry.getRegisteredCommands()) {
            helpMessageBuilder.append(botCommand.toString()).append("\n\n");
        }

        SendMessage helpMessage = new SendMessage();
        helpMessage.setChatId(chat.getId().toString());
        helpMessage.enableHtml(true);
        helpMessage.setText(helpMessageBuilder.toString());

        try {
            absSender.execute(helpMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
}
}
