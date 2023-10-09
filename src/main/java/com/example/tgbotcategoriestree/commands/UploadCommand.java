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

/**
 * Service class to manage uploading Excel file and save categories from it into BD
 *
 * @author enovak89
 */
@Service
public class UploadCommand extends BotCommandCustom {
    public static boolean UPLOAD_PERMISSION = false;

    /**
     * String builder contains result message text
     */
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

    /**
     * Method to get file from Telegram API, to map file to work book and to save its categories into BD
     *
     * @param absSender
     * @param message   - message from update
     * @throws TelegramApiException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void processMessageWithDocument(AbsSender absSender, Message message) {
        if (checkDocumentAndPermission(message)) {
            try {
                //Getting url, where is required JSON
                URL urlToGetFilePath = new URL("https://api.telegram.org/bot" + BotConfig.BOT_TOKEN
                        + "/getFile?file_id=" + message.getDocument().getFileId());

                //Getting JSON with key result, where is object with path file
                BufferedReader in = new BufferedReader(new InputStreamReader(urlToGetFilePath.openStream()));
                String stringWithKeyResult = in.readLine();
                JSONObject jsonWithKeyResult = new JSONObject(stringWithKeyResult);

                //Getting JSON with key file_path
                JSONObject jsonWithFilePath = jsonWithKeyResult.getJSONObject("result");

                //Getting file path
                String filePath = jsonWithFilePath.getString("file_path");

                //Downloading file
                TelegramFileDownloader fileDownloader = new TelegramFileDownloader(BotConfig::getBotToken);
                File file = fileDownloader.downloadFile(filePath);

                //Mapping to work book
                Workbook workbook = new XSSFWorkbook(new FileInputStream(file));

                //Recording categories into DB
                fileService.recordDataInDbFromWorkBook(workbook);

                UPLOAD_PERMISSION = false;
                messageText.replace(0, messageText.length(), "Uploading is completed successfully");
                uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
            } catch (IOException | TelegramApiException | IllegalArgumentException e) {
                UPLOAD_PERMISSION = false;
                messageText.replace(0, messageText.length(), e.getMessage());
                uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
                logger.error(e.getMessage());
            }
        } else {
            uploadCommandResultAnswer(absSender, message.getChat(), messageText.toString());
        }
    }

    /**
     * Method to check required document format and upload permission
     *
     * @param message - message from update
     * @return boolean checking result
     */
    private boolean checkDocumentAndPermission(Message message) {
        if (!UPLOAD_PERMISSION) {
            messageText.replace(0, messageText.length(), "Sorry, not ready to download." +
                    " Run the /upload command before sending the file");
            return false;
        }
        if (!message.getDocument().getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            messageText.replace(0, messageText.length(), "Sorry, document format is not .XLSX");
            return false;
        }
        return true;
    }

    /**
     * Method to set inform message when user run uploadCommand
     *
     * @param absSender
     * @param user      - user from message
     * @param chat      - chat from message
     * @param arguments - parameters of command
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        messageText.replace(0, messageText.length(), "Please, in your response message send XLSX file");
        uploadCommandResultAnswer(absSender, chat, messageText.toString());

        UPLOAD_PERMISSION = true;
        messageText.replace(0, messageText.length(), "Ready to download");
        uploadCommandResultAnswer(absSender, chat, messageText.toString());
    }

    /**
     * Method to send message with uploadCommand result text
     *
     * @param absSender
     * @param chat      - chat from message
     * @param text      - message text to send
     * @throws TelegramApiException
     */
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
