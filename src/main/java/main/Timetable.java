package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.*;

public class Timetable {


    private static Map<Integer, String> dayNumberToText = new HashMap<>(){{
        put(2, "Понедельник");
        put(3, "Вторник");
        put(4, "Среда");
        put(5, "Четверг");
        put(6, "Пятница");
        put(7, "Суббота");
        put(1, "Воскресенье");
    }};

    private static Map<Integer, String> intToMonth = new HashMap<>(){{
        put(0, "января");
        put(1, "февраля");
        put(2, "марта");
        put(3, "апреля");
        put(4, "мая");
        put(5, "июня");
        put(6, "июля");
        put(7, "августа");
        put(8, "сентября");
        put(9, "октября");
        put(10, "ноября");
        put(11, "декабря");
    }};

    public static SendMessage getTimetable(UserInfo user, int dayShift) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);

        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yekaterinburg"));
        c.add(Calendar.DATE, dayShift);
        String dayOfWeek = dayNumberToText.get(c.get(Calendar.DAY_OF_WEEK));
        String timetable = String.format("***%s, %d %s***\n",
                dayOfWeek,
                c.get(Calendar.DAY_OF_MONTH),
                intToMonth.get(c.get(Calendar.MONTH)));
        ArrayList<String> lessonList = SQLCommands.GetLessonList(user, dayOfWeek);
        if (lessonList.size() == 0) {
            timetable = timetable + "\n___Нет занятий___";
        }
        else {
            for (String lesson : lessonList) {
                if(lesson != null) {
                    timetable = timetable + String.format("\n%s", lesson);
                }
            }
        }

        msg.setText(timetable);
        return msg;
    }
}