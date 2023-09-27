package com.example.tgbotcategoriestree.registerBot;

import com.example.tgbotcategoriestree.updatesHandlers.EchoHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class RegisterBot {

    @PostConstruct
    public void init() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(new EchoHandler());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
