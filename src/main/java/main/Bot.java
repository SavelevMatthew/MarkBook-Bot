package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    private static Logger log = Logger.getLogger(Bot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String userId = update.getMessage().getChatId().toString();
        UserInfo user = SQLCommands.GetUserInfo(Integer.parseInt(userId));
        if (user.status.equals("not_exists")) {
            user.status = UserStatus.HELLO;
            SQLCommands.AddUserToSQL(user);
        }
        user.msg_text = message;

        SendMessage sendMessage = new SendMessage();

        switch (user.status) {
            case "hello":   sendMessage = Registration.RegisterUser(user); break;
            case "get_group_id":   sendMessage = Registration.AddUserToGroup(user); break;
            case "default" : sendMessage = BotCommands.FromMainMenu(user); break;
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
                log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        try
        {
            Scanner sc = new Scanner(new File("C:\\Users\\user\\IdeaProjects\\Markbook-Bot\\src\\main\\java\\main\\config"));
            String [] splitted;
            while(sc.hasNext()) {
                splitted = sc.nextLine().split(" ");
                return splitted[0];
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }

        return "";
    }

    @Override
    public String getBotToken() {
        try
        {
            Scanner sc = new Scanner(new File("C:\\Users\\user\\IdeaProjects\\Markbook-Bot\\src\\main\\java\\main\\config"));
            String [] splitted;
            while(sc.hasNext()) {
                splitted = sc.nextLine().split(" ");
                return splitted[1];
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }

        return "";
    }
}
