package com.example.tgbotcategoriestree;

/**
 * Class to configure bot name and bot token
 *
 * @author enovak89
 */
public class BotConfig {
    public static final String BOT_TOKEN = "6459950492:AAHRNR8kIvY5IsXHnSZvijkuUxJGTK119Tg";
    public static final String BOT_NAME = "CategoriesTreeBot";

    /**
     * Method to use in {@link org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader} constructor,
     * which needs an Supplier<String> botTokenSupplier
     *
     * @return String with bot token
     */
    public static String getBotToken() {
        return BOT_TOKEN;
    }
}
