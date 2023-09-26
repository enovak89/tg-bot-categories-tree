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

//        try {
//        ApiContextInitializer.init();
//        TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
//        try {
//            // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
//            telegramBotsApi.registerBot(new ChannelHandlers());
//        } catch (TelegramApiException e) {
//            System.out.println(e.getMessage());
//        }
//    } catch (Exception e) {
//        System.out.println(e.getMessage());
//    }
    }
}
