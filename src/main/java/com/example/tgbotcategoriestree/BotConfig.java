package com.example.tgbotcategoriestree;

public class BotConfig {
    public static final String BOT_TOKEN = "6459950492:AAHRNR8kIvY5IsXHnSZvijkuUxJGTK119Tg";
    public static final String BOT_NAME = "CategoriesTreeBot";

    /**
     * Method to use in TelegramFileDownloader.class constructor, which needs an Supplier<String> botTokenSupplier
     *
     * @return String with bot token
     */
    public static String getBotToken() {
        return BOT_TOKEN;
    }
}
