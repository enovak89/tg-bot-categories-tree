package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.BotConfig;
import com.example.tgbotcategoriestree.telegramBotsLibraryCustomizedClasses.BotCommandCustom;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;

@Service
public class UploadCommand extends BotCommandCustom {
    public static boolean UPLOAD_PERMISSION = false;

    private final Logger logger = LoggerFactory.getLogger(UploadCommand.class);

    public UploadCommand() {
        super("/upload", "Upload excel file containing the entire categories tree to save it in BD");
    }

    public void processMessageWithDocument(Message message) {
        System.out.println("UPLOAD_PERMISSION from processMessageWithDocument = " + UPLOAD_PERMISSION);
        if (message.hasDocument() && UPLOAD_PERMISSION) {
            System.out.println("doc ok");
            try {
                URL url = new URL("https://api.telegram.org/bot" + BotConfig.BOT_TOKEN
                        + "/getFile?file_id=" + message.getDocument().getFileId());
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String res = in.readLine();
                JSONObject jresult = new JSONObject(res);
                JSONObject path = jresult.getJSONObject("result");
                String file_path = path.getString("file_path");
                TelegramFileDownloader fileDownloader = new TelegramFileDownloader(BotConfig::getBotToken);
                File file = fileDownloader.downloadFile(file_path);
                Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
                System.out.println("workbook.getSheetAt(0).getRow(0).getCell(0) = " + workbook.getSheetAt(0).getRow(0).getCell(0));
                System.out.println("workbook.getSheetAt(0).getRow(0).getCell(1) = " + workbook.getSheetAt(0).getRow(0).getCell(1));
                UPLOAD_PERMISSION = false;
            } catch (IOException e) {
                System.out.println("io ex");
            } catch (TelegramApiException ex) {
                System.out.println("API ex");
            }
        } else {
            System.out.println("doc not ok");
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage getUploadCommandMessage = new SendMessage();
        getUploadCommandMessage.setChatId(chat.getId());
        getUploadCommandMessage.setText("Please, in your response message send XLSX file," +
                " where put root categories in the first column, its child categories - in the next columns");
        try {
            absSender.execute(getUploadCommandMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }

        getUploadCommandMessage.setText("Ready to download");
        try {
            absSender.execute(getUploadCommandMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
        UPLOAD_PERMISSION = true;
    }
}
