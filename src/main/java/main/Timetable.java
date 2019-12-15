package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
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

    final private static Map<String, String> daySmallToText = Map.ofEntries(
            entry("ПН", "Понедельник"),
            entry("ВТ", "Вторник"),
            entry("СР", "Среда"),
            entry("ЧТ", "Четверг"),
            entry("ПТ", "Пятница"),
            entry("СБ", "Суббота"),
            entry("ВС", "Воскресенье")
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
        Boolean iseven = c.get(Calendar.WEEK_OF_YEAR)%2 == 0;

        ArrayList<String> lessonList = SQLCommands.GetLessonListByWeekDay(user, weekDay, iseven);
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

    static SendMessage EditTimetable(UserInfo user) {
        if ("⬅️ Вернуться в главное меню".equals(user.msg_text)) {
            return  BotCommands.ReturnToMainMenu(user);
        }

        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        switch(user.status) {
            case SETTINGS: {
                user.status = UserStatus.GET_WEEKDAY;
                msg.setText("Выбери день недели:");
                Keyboards.WeekDays(msg);
                SQLCommands.UpdateUserInfo(user);
                return msg;
            }
            case GET_WEEKDAY: {
                msg.setText("Выбери неделю: ");
                user.status = UserStatus.GET_WEEK_TYPE;
                user.properties = daySmallToText.get(user.msg_text);
                SQLCommands.UpdateUserInfo(user);
                Keyboards.Weeks(msg);
                return msg;
            }
            case GET_WEEK_TYPE: {
                Boolean iseven = "Четная".equals(user.msg_text);
                String weekName = (iseven) ? ", четная неделя" : ", нечетная неделя";
                msg.setText(String.format("Редактирование расписания на ***%s***.\n\nОтправь номер первой пары:\n", user.properties + weekName));

                user.status = UserStatus.EDIT_FIRSTLESSON_NUMBER;
                user.properties = user.properties + ", " + iseven.toString();
                SQLCommands.UpdateUserInfo(user);
                return msg;
            }
            case EDIT_FIRSTLESSON_NUMBER: {
                int lessonNumber;
                try {
                    lessonNumber = Integer.parseInt(user.msg_text);
                } catch (NumberFormatException a) {
                    msg.setText("***⚠️Номер первой пары — это число от 1 до 7***\nОтправь номер первой пары ещё раз:");
                    return msg;
                }

                if (lessonNumber > 7) {
                    msg.setText("***⚠️Номер первой пары — это число от 1 до 7***\nОтправь номер первой пары ещё раз:");
                    return msg;
                }

                String[] splitted = user.properties.split(", ");
                String weekDay = splitted[0];
                Boolean iseven = Boolean.valueOf(splitted[1]);

                if (SQLCommands.GetLessonListByWeekDay(user, weekDay, iseven).isEmpty()) {
                    user.status = UserStatus.valueOf(String.format("EDIT_LESSON%d", lessonNumber));
                    SQLCommands.InitTimetableDay(user, user.msg_text);
                } else {
                    user.status = UserStatus.valueOf(String.format("EDIT_LESSON%d", lessonNumber));
                    SQLCommands.EditTimetableDayFirstLesson(user, user.msg_text);
                }

                msg.setText("Выбери название из списка или введи новое\nв формате эмодзи+название:\n___\uD83D\uDCBB Объектно-ориентированное программирование___\n\n***После ввода последней пары нажми ✅ Готово.*** Бот перейдет к заполнению расписания на следующий день");
                Keyboards.LessonList(user, msg, false, true, true);
                SQLCommands.UpdateUserInfo(user);

                return msg;
            }
        }

        if ("✅ Готово".equals(user.msg_text) || user.status == UserStatus.CLOSE_EDITING) {
            user.status = UserStatus.DEFAULT;
            msg.setText("\uD83D\uDC4C\uD83C\uDFFB Редактирование расписания завершено");
            Keyboards.MainMenu(msg, user.isAdmin);
            SQLCommands.UpdateUserInfo(user);
            return msg;
        }

        int lessonNumber = 0;
                switch(user.status) {
            case EDIT_LESSON1: lessonNumber = 1; user.status = UserStatus.EDIT_LESSON2; break;
            case EDIT_LESSON2: lessonNumber = 2; user.status = UserStatus.EDIT_LESSON3; break;
            case EDIT_LESSON3: lessonNumber = 3; user.status = UserStatus.EDIT_LESSON4; break;
            case EDIT_LESSON4: lessonNumber = 4; user.status = UserStatus.EDIT_LESSON5; break;
            case EDIT_LESSON5: lessonNumber = 5; user.status = UserStatus.EDIT_LESSON6; break;
            case EDIT_LESSON6: lessonNumber = 6; user.status = UserStatus.EDIT_LESSON7; break;
            case EDIT_LESSON7: lessonNumber = 7; user.status = UserStatus.CLOSE_EDITING; break;
        }

                Registration.AddLessonIfNotExists(user);
                SQLCommands.UpdateTimetable(user, lessonNumber, user.msg_text);

                msg.setText("Урок добавлен. Отправь следующий:");
                SQLCommands.UpdateUserInfo(user);
                Keyboards.LessonList(user, msg, false, true, true);
                return msg;
    }
}