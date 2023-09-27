package com.example.tgbotcategoriestree.updatesHandlers;

import com.example.tgbotcategoriestree.BotConfig;
import com.example.tgbotcategoriestree.service.CategoryService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@NoArgsConstructor(force = true)
public class EchoHandler extends TelegramLongPollingBot {

    private final CategoryService categoryService;

    public EchoHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {

                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.setChatId(message.getChatId().toString());
                sendMessageRequest.setText("you said: " + message.getText());
                try {
                    execute(sendMessageRequest);
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }
}