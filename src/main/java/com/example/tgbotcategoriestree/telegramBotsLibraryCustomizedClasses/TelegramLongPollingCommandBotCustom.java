package com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses;


import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This class adds command functionality to the TelegramLongPollingBot
 *
 * @author Timo Schulz (Mit0x2)
 * <p>
 * Customized enovak89
 */
public abstract class TelegramLongPollingCommandBotCustom extends TelegramLongPollingBot implements CommandBot, ICommandRegistry {
    private final CommandRegistry commandRegistry;

    /**
     * Creates a TelegramLongPollingCommandBotCustom using default options
     * Use ICommandRegistry's methods on this bot to register commands
     */
    @Deprecated
    public TelegramLongPollingCommandBotCustom() {
        this(new DefaultBotOptions());
    }

    /**
     * Creates a TelegramLongPollingCommandBotCustom with custom options and allowing commands with
     * usernames
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options Bot options
     */
    @Deprecated
    public TelegramLongPollingCommandBotCustom(DefaultBotOptions options) {
        this(options, true);
    }

    /**
     * Creates a TelegramLongPollingCommandBotCustom
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options                   Bot options
     * @param allowCommandsWithUsername true to allow commands with parameters (default),
     *                                  false otherwise
     */
    @Deprecated
    public TelegramLongPollingCommandBotCustom(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    /**
     * Creates a TelegramLongPollingCommandBotCustom using default options
     * Use ICommandRegistry's methods on this bot to register commands
     */
    public TelegramLongPollingCommandBotCustom(String botToken) {
        this(new DefaultBotOptions(), botToken);
    }

    /**
     * Creates a TelegramLongPollingCommandBotCustom with custom options and allowing commands with
     * usernames
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options Bot options
     */
    public TelegramLongPollingCommandBotCustom(DefaultBotOptions options, String botToken) {
        this(options, true, botToken);
    }

    /**
     * Creates a TelegramLongPollingCommandBotCustom
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options                   Bot options
     * @param allowCommandsWithUsername true to allow commands with parameters (default),
     *                                  false otherwise
     */
    public TelegramLongPollingCommandBotCustom(DefaultBotOptions options, boolean allowCommandsWithUsername, String botToken) {
        super(options, botToken);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    @Override
    public final void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    //we have received a not registered command, handle it as invalid
                    processInvalidCommandUpdate(update);
                }
                return;
            }
        }
        processNonCommandUpdate(update);
    }

    @Override
    public final boolean register(IBotCommand botCommand) {
        return commandRegistry.register(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> registerAll(IBotCommand... botCommands) {
        return commandRegistry.registerAll(botCommands);
    }

    @Override
    public final boolean deregister(IBotCommand botCommand) {
        return commandRegistry.deregister(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> deregisterAll(IBotCommand... botCommands) {
        return commandRegistry.deregisterAll(botCommands);
    }

    @Override
    public final Collection<IBotCommand> getRegisteredCommands() {
        return commandRegistry.getRegisteredCommands();
    }

    @Override
    public void registerDefaultAction(BiConsumer<AbsSender, Message> defaultConsumer) {
        commandRegistry.registerDefaultAction(defaultConsumer);
    }

    @Override
    public final IBotCommand getRegisteredCommand(String commandIdentifier) {
        return commandRegistry.getRegisteredCommand(commandIdentifier);
    }

    /**
     * @return Bot username
     */
    @Override
    public abstract String getBotUsername();
}

