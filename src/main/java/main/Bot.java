package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    final private static Logger BotLogger = Logger.getLogger(Bot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String userId = update.getMessage().getChatId().toString();
        UserInfo user = SQLCommands.GetUserInfo(Integer.parseInt(userId));
        if (user.status == UserStatus.NOT_EXISTS) {
            user.status = UserStatus.HELLO;
            SQLCommands.AddUserToSQL(user);
        }
        user.msg_text = message;

        SendMessage sendMessage = new SendMessage();

        switch (user.status) {
            case HELLO:
                sendMessage = Registration.RegisterUser(user);
                break;
            case GET_GROUPID:
                sendMessage = Registration.AddUserToGroup(user);
                break;
            case DEFAULT:
                sendMessage = BotCommands.FromMainMenu(user);
                break;
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            BotLogger.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    private String getBotInfo(int parameter) {
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "main", "resources", "config");
            Scanner sc = new Scanner(new File(path.toString()));
            String[] splitted;
            if (sc.hasNext()) {
                splitted = sc.nextLine().split(" ");
                return splitted[parameter];
            }
        } catch (FileNotFoundException e) {
            BotLogger.log(Level.SEVERE, "Exception: ", e.toString());
        }

        return "";
    }

    @Override
    public String getBotUsername() {
        return getBotInfo(0);
    }

    @Override
    public String getBotToken() {
        return getBotInfo(1);
    }
}
