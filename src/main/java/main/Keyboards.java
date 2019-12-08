package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

class Keyboards {
    static synchronized void StartRegistration(SendMessage sendMessage) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("Создать группу");
        buttonTexts.add("Присоединиться к существующей группе");

        ReplyKeyboardMarkup replyKeyboard = CreateBlankKeyboard(buttonTexts);
        sendMessage.setReplyMarkup(replyKeyboard);
    }

    static synchronized void EndRegistration(SendMessage sendMessage) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("✅ Завершить заполнение расписания");

        ReplyKeyboardMarkup replyKeyboard = CreateBlankKeyboard(buttonTexts);
        sendMessage.setReplyMarkup(replyKeyboard);
    }

    static synchronized void LessonList(UserInfo user, SendMessage msg, boolean backButton, boolean noLessonButton, boolean doneButton) {
        ArrayList<String> lessonsList = SQLCommands.GetLessonList(user);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        if (backButton) { lessonsList.add("⬅️ Назад"); }
        if (doneButton) { lessonsList.add("\uD83E\uDD37\u200D♂️ Окно"); }
        if (noLessonButton) { lessonsList.add("✅ Готово"); }

        for (int i = 0; i < lessonsList.size(); i+=2){
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(lessonsList.get(i)));
            if (i+1 < lessonsList.size()) {
                row.add(new KeyboardButton(lessonsList.get(i+1)));
            }

            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        msg.setReplyMarkup(replyKeyboardMarkup);
    }

    static synchronized void MainMenu(SendMessage sendMessage, boolean isAdmin) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("Расписание на сегодня");
        buttonTexts.add("Расписание на завтра");

        if(isAdmin) {
            buttonTexts.add("Добавить домашнее задание");
        }

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

    static InlineKeyboardMarkup InlineButton(CallbackQuery callbackQuery) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//        List<InlineKeyboardButton> row = new ArrayList<>();
//        switch(callbackQuery.getData()) {
//            case "hw": row.add(new InlineKeyboardButton("⬅️ Вернуться к расписанию")); break;
//            case "tt": row.add(new InlineKeyboardButton("Домашнее задание")); break;
//        }
//
//        keyboard.add(row);
//
//        inlineKeyboardMarkup.setKeyboard(keyboard);
//
//        return inlineKeyboardMarkup;

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();

        switch (callbackQuery.getData()) {
            case "hw": keyboardButtonsRow.add(new InlineKeyboardButton().setText("⬅️ Вернуться к расписанию").setCallbackData("tt")); break;
            case "tt": keyboardButtonsRow.add(new InlineKeyboardButton().setText("Домашнее задание").setCallbackData("hw")); break;
        }

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return  inlineKeyboardMarkup;
    }
}
