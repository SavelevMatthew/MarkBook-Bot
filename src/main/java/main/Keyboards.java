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

    static synchronized void EndRegistration(SendMessage sendMessage, boolean copy) {
        ArrayList<String> buttonTexts = new ArrayList<>();

        if (copy) {
            buttonTexts.add("\uD83D\uDCD1 Скопировать расписание четной недели");
            buttonTexts.add(" Следующий день");
        }
        else {
            buttonTexts.add(" Нечетная неделя");
        }
        buttonTexts.add(" Завершить заполнение расписания");

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

        if (backButton) { lessonsList.add(" Назад"); }
        if (doneButton) { lessonsList.add("\uD83E\uDD37\u200D Окно"); }
        if (noLessonButton) { lessonsList.add(" Готово"); }

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

    static synchronized void WeekDays(SendMessage msg) {
        List<String> weekDays = List.of("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС");
        CreateOneRowKeyboard(msg, weekDays);
    }

    static synchronized void Weeks(SendMessage msg) {
        List<String> weeks = List.of("Четная", "Нечетная");
        CreateOneRowKeyboard(msg, weeks);
    }

    static synchronized void CreateOneRowKeyboard(SendMessage msg, List<String> elements) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow row = new KeyboardRow();
        for(String element : elements) {
            row.add(element);
        }

        keyboard.add(row);
        KeyboardRow rowMenu = new KeyboardRow();
        rowMenu.add(" Вернуться в главное меню");
        keyboard.add(rowMenu);

        replyKeyboardMarkup.setKeyboard(keyboard);
        msg.setReplyMarkup(replyKeyboardMarkup);
    }

    static synchronized void MainMenu(SendMessage sendMessage, boolean isAdmin) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("Расписание на сегодня");
        buttonTexts.add("Расписание на завтра");
        buttonTexts.add("Все домашние задания");

        if(isAdmin) {
            buttonTexts.add("Настройки");
        }

        ReplyKeyboardMarkup replyKeyboard = CreateBlankKeyboard(buttonTexts);
        sendMessage.setReplyMarkup(replyKeyboard);
    }

    static synchronized void Settings(long userid, SendMessage sendMessage) {
        ArrayList<String> buttonTexts = new ArrayList<>();
        buttonTexts.add("Добавить домашнее задание");
        buttonTexts.add("Редактировать расписание");
        buttonTexts.add("Добавить файл");
        buttonTexts.add("Получить код группы");
        if (userid==409216737) {
            buttonTexts.add("Отправить сообщение всем администраторам");
        }
        buttonTexts.add(" Вернуться в главное меню");

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
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();

        switch (callbackQuery.getData()) {
            case "hw": keyboardButtonsRow.add(new InlineKeyboardButton().setText(" Вернуться к расписанию").setCallbackData("tt")); break;
            case "tt": keyboardButtonsRow.add(new InlineKeyboardButton().setText("Домашнее задание").setCallbackData("hw")); break;
        }

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return  inlineKeyboardMarkup;
    }
}