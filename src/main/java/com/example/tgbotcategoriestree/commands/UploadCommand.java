package com.example.tgbotcategoriestree.commands;

import com.example.tgbotcategoriestree.BotConfig;
import com.example.tgbotcategoriestree.services.FileService;
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

    private static final StringBuilder messageText = new StringBuilder();

    private final Logger logger = LoggerFactory.getLogger(UploadCommand.class);

    private static FileService fileService;

    private UploadCommand(FileService fileService) {
        super("/upload", "Upload excel file containing the entire categories tree to save it in BD");
        UploadCommand.fileService = fileService;
    }

    public static UploadCommand getUploadCommand() {
        return new UploadCommand(fileService);
    }

    public void processMessageWithDocument(AbsSender absSender, Message message) {
        if (UPLOAD_PERMISSION) {
            try {
                URL urlToGetFilePath = new URL("https://api.telegram.org/bot" + BotConfig.BOT_TOKEN
                        + "/getFile?file_id=" + message.getDocument().getFileId());
                BufferedReader in = new BufferedReader(new InputStreamReader(urlToGetFilePath.openStream()));
                String stringWithKeyResult = in.readLine();
                JSONObject jsonWithKeyResult = new JSONObject(stringWithKeyResult);
                JSONObject jsonWithFilePath = jsonWithKeyResult.getJSONObject("result");
                String filePath = jsonWithFilePath.getString("file_path");
                TelegramFileDownloader fileDownloader = new TelegramFileDownloader(BotConfig::getBotToken);
                File file = fileDownloader.downloadFile(filePath);
                Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
                fileService.recordDataInDbFromWorkBook(workbook);

                UPLOAD_PERMISSION = false;
                messageText.replace(0, messageText.length(), "Downloading is completed successfully");
                uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
            } catch (IOException | TelegramApiException | IllegalArgumentException e) {
                UPLOAD_PERMISSION = false;
                messageText.replace(0, messageText.length(), e.getMessage());
                uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
                logger.error(e.getMessage());
            }
        } else {
            messageText.replace(0, messageText.length(), "Sorry, not ready to download." +
                    " Run the /upload command before sending the file");
            uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        messageText.replace(0, messageText.length(), "Please, in your response message send XLSX file," +
                " where at the first worksheet put root categories in the first column, its child categories - in the next columns");
        uploadCommandResultAnswer(absSender, chat, messageText.toString());

        UPLOAD_PERMISSION = true;
        messageText.replace(0, messageText.length(), "Ready to download");
        uploadCommandResultAnswer(absSender, chat, messageText.toString());
    }

    private void uploadCommandResultAnswer(AbsSender absSender, Chat chat, String text) {

        SendMessage uploadCommandResultMessage = new SendMessage();
        uploadCommandResultMessage.setChatId(chat.getId());
        uploadCommandResultMessage.setText(text);

        try {
            absSender.execute(uploadCommandResultMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }
}
