package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

//        if(Arrays.asList(UserStatus.GET_LESSON1,
//                UserStatus.GET_LESSON2,
//                UserStatus.GET_LESSON3,
//                UserStatus.GET_LESSON4,
//                UserStatus.GET_LESSON5,
//                UserStatus.GET_LESSON6,
//                UserStatus.GET_LESSON7).contains(user.status)) {
//            sendMessage = Registration.UpdateTimetable(user);
//        }

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
            case GET_GROUPNAME:
                sendMessage = Registration.RegisterGroup(user);
                break;
            case GET_FIRSTLESSON_NUMBER:
                sendMessage = Registration.SetFirstLesson(user);
                break;
            case GET_LESSON1:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON2:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON3:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON4:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON5:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON6:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON7:
                sendMessage = Registration.UpdateTimetable(user);
                break;
            case GET_LESSON_NAME:
                sendMessage = Hometasks.UpdateHometask(user);
                break;
            case GET_HOMETASK:
                sendMessage = Hometasks.UpdateHometask(user);
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
