package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static java.util.Map.entry;

class Timetable {


    final private static Map<Integer, String> dayNumberToText = Map.ofEntries(
        entry(2, "Понедельник"),
        entry(3, "Вторник"),
        entry(4, "Среда"),
        entry(5, "Четверг"),
        entry(6, "Пятница"),
        entry(7, "Суббота"),
        entry(1, "Воскресенье")
    );

    final private static Map<Integer, String> intToMonth = Map.ofEntries(
            entry(0, "января"),
            entry(1, "февраля"),
            entry(2, "марта"),
            entry(3, "апреля"),
            entry(4, "мая"),
            entry(5, "июня"),
            entry(6, "июля"),
            entry(7, "августа"),
            entry(8, "сентября"),
            entry(9, "октября"),
            entry(10, "ноября"),
            entry(11, "декабря")
        );

    static SendMessage getTimetable(UserInfo user, int dayShift) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);

        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yekaterinburg"));
        c.add(Calendar.DATE, dayShift);

        String weekDay = dayNumberToText.get(c.get(Calendar.DAY_OF_WEEK));
        int date = c.get(Calendar.DAY_OF_MONTH);
        String month = intToMonth.get(c.get(Calendar.MONTH));

        String timetable = String.format("***%s, %d %s***\n", weekDay, date, month);

        ArrayList<String> lessonList = SQLCommands.GetLessonListByWeekDay(user, weekDay);
        if (lessonList.isEmpty()) {
            timetable = timetable + "\n___Нет занятий___";
        }
        else {
            timetable = timetable + String.format("___Учимся с %s пары___\n ", SQLCommands.GetFirstLessonNumber(user, weekDay));
            for (String lesson : lessonList) {
                if(lesson != null) {
                    timetable = timetable + String.format("\n%s", lesson);
                }
            }

            InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(new InlineKeyboardButton().setText("Домашнее задание").setCallbackData("hw"));
            List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
            rowList.add(keyboardButtonsRow);
            inlineKeyboardMarkup.setKeyboard(rowList);
            msg.setReplyMarkup(inlineKeyboardMarkup);
        }

        msg.setText(timetable);
        return msg;
    }

    static String GetHometaskByDay(UserInfo user, Message originalMsg) {
        String [] lessons = originalMsg.getText().split("\n");
        String result = String.format("***%s***\n___%s___", lessons[0], lessons[1]);
        for (String lesson: lessons) {
            String hometask = SQLCommands.GetHometask(user, lesson);
            if (!hometask.isEmpty()) {
                result = result + String.format("\n%s\n___%s___", lesson, hometask);
            }
        }
        return result;
    }

    static String ReturnTimetableInEdited(UserInfo user, Message editedMsg) {
        ArrayList<String> lessons = SQLCommands.GetLessonList(user);
        String [] rawLessons = editedMsg.getText().split("\n");
        String result = String.format("***%s***\n___%s___", rawLessons[0], rawLessons[1]);
        for(String lesson: rawLessons) {
            if(lessons.contains(lesson)) {
                result += String.format("\n%s", lesson);
            }
        }
        return result;
    }
}