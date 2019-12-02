package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {
    public static synchronized void StartRegistration(SendMessage sendMessage) {
        ArrayList<String> buttonTexts = new ArrayList<>();
//        buttonTexts.add("Создать группу");
        buttonTexts.add("Присоединиться к существующей группе");

        ReplyKeyboardMarkup replyKeyboard = CreateBlankKeyboard(buttonTexts);
        sendMessage.setReplyMarkup(replyKeyboard);
    }

    public static synchronized void MainMenu(SendMessage sendMessage) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("Расписание на сегодня");
        buttonTexts.add("Расписание на завтра");

        ReplyKeyboardMarkup replyKeyboard = CreateBlankKeyboard(buttonTexts);
        sendMessage.setReplyMarkup(replyKeyboard);
    }

    private static ReplyKeyboardMarkup CreateBlankKeyboard(ArrayList <String> buttonTexts) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String buttonText: buttonTexts) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(buttonText));
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
