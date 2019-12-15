package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    final private static Logger BotLogger = Logger.getLogger(Bot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (null != callbackQuery) {
            String userId = callbackQuery.getMessage().getChatId().toString();
            UserInfo user = SQLCommands.GetUserInfo(Integer.parseInt(userId));
            try {
                execute(CallbackHandler.StartHandling(callbackQuery, user));
            } catch (TelegramApiException e) {
                BotLogger.log(Level.SEVERE, "Exception: ", e.toString());
            }
            return;

        }

        String userId = update.getMessage().getChatId().toString();
        UserInfo user = SQLCommands.GetUserInfo(Integer.parseInt(userId));
        if (user.status == UserStatus.NOT_EXISTS) {
            user.status = UserStatus.HELLO;
            SQLCommands.AddUserToSQL(user);
        }
        user.msg_text = update.getMessage().getText();

        SendMessage sendMessage = new SendMessage();

        Boolean emergencyCommand = false;
        if("/help".equals(user.msg_text)) {
            sendMessage = BotCommands.SendHelp(user);
            emergencyCommand = true;
        }

        if ("/support".equals(user.msg_text)) {
            sendMessage = BotCommands.InitCallToSupport(user);
            emergencyCommand = true;
        }

        if (emergencyCommand) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                BotLogger.log(Level.SEVERE, "Exception: ", e.toString());
            }
            return;
        }

        boolean moreMessages = false;
        List<SendMessage> messages = new ArrayList<>();

        switch (user.status) {
            case GET_SUPPORT_TEXT:
                sendMessage = BotCommands.CallToSupport(user, update.getMessage().getFrom().getUserName());
                break;
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
            case SETTINGS: {
                if ("Отправить сообщение всем администраторам".equals(user.msg_text)) {
                    messages = Settings.SendToAllAdmins(user);
                    moreMessages = true;
                } else {
                    sendMessage = Settings.FromSettings(user);
                }
                break;
            }
            case EDIT_LESSON1:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON2:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON3:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON4:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON5:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON6:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_LESSON7:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case EDIT_FIRSTLESSON_NUMBER:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case GET_WEEKDAY:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case CLOSE_EDITING:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case GET_WEEK_TYPE:
                sendMessage = Timetable.EditTimetable(user);
                break;
            case GET_MSG_TO_ALL_ADMINS: {
                messages = Settings.SendToAllAdmins(user);
                moreMessages = true;
                break;
            }
            case APPROVE_SENDING_MSG_TO_ALL_ADMINS: {
                messages = Settings.SendToAllAdmins(user);
                moreMessages = true;
                break;
            }
        }

        if (moreMessages) {
            for (SendMessage msg : messages) {
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    BotLogger.log(Level.SEVERE, "Exception: ", e.toString());
                }
            }
            return;
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