package main;

import java.util.*;

public class Timetable {
    private static String monday = "***Понедельник, %s***\n___Учимся с 9:00___\n" +
            "\n" +
            "\uD83D\uDCBB ООП, практика /517\n" +
            "\uD83D\uDCBB ООП /632";
    private static String tuesday = "***Вторник, %s***\n___Учимся с 12:50___\n" +
            "\n" +
            "\uD83E\uDDEE Дискретная математика /532\n" +
            "\uD83D\uDCFA Архитектура ЭВМ /632\n" +
            "\uD83C\uDDEC\uD83C\uDDE7 Английский язык";
    private static  String wednesday = "***Среда, %s***\n___Учимся с 10:00___\n" +
            "\n" +
            "\uD83C\uDFC3\u200D♂️ Физкультура\n" +
            "\uD83E\uDD37\u200D♂️ Окно\n" +
            "\uD83E\uDDEE Дискретная математика, практика /612\n" +
            "\uD83E\uDDEE Дискретная математика /532";
    private static String thursday = "***Четверг, %s***\n___Учимся с 12:50___\n" +
            "\n" +
            "\uD83C\uDDEC\uD83C\uDDE7 Английский язык\n" +
            "\uD83D\uDCC8 Матанализ /532\n" +
            "\uD83D\uDDA5 Сети /150\n" +
            "\uD83D\uDCC8 Матанализ, практика /605";
    private static String friday = "***Пятница, %s***\n___Учимся с 9:00___\n" +
            "\n" +
            "\uD83D\uDC0D Языки сценариев /150\n" +
            "\uD83D\uDCC8 Матанализ, практика /622a\n" +
            "\uD83D\uDCC8 Матанализ /621\n" +
            "\uD83E\uDD37\u200D♂️ Окно\n" +
            "\uD83C\uDFC3\u200D♂️ Физкультура";
    private static String saturday = "Сегодня пар нет. Расписание на ближайший день:\n" +
            "\n" + monday;
    private static String sunday = "Сегодня пар нет. Расписание на ближайший день:\n" +
            "\n" + monday;


    private static Map<Integer, String> dayNumberToText = new HashMap<>(){{
        put(1, Timetable.saturday);
        put(2, Timetable.monday);
        put(3, Timetable.tuesday);
        put(4, Timetable.wednesday);
        put(5, Timetable.thursday);
        put(6, Timetable.friday);
        put(7, Timetable.sunday);
    }};

    private static Map<Integer, String> intToMonth = new HashMap<>(){{
        put(0, " января");
        put(1, " февраля");
        put(2, " марта");
        put(3, " апреля");
        put(4, " мая");
        put(5, " июня");
        put(6, " июля");
        put(7, " августа");
        put(8, " сентября");
        put(9, " октября");
        put(10, " ноября");
        put(11, " декабря");
    }};

    public static String createTimetable(int dayShift) {
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yekaterinburg"));
        final Calendar editedCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yekaterinburg"));
        c.add(Calendar.DATE, dayShift);
        editedCalendar.add(Calendar.DATE, dayShift);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        dayShift = 0;
        while (dayNumberToText.get(editedCalendar.get(Calendar.DAY_OF_WEEK)) == "null")
        {

            dayShift += 1;
            editedCalendar.add(Calendar.DATE, dayShift);
        }

        String timetable = "";
        if (dayShift != 0)
        {
            c.add(Calendar.DATE, dayShift);
            timetable = "___" + dayOfMonth + intToMonth.get(month) + " занятий нет. Расписание на ближайший день:___\n\n";
        }
        String date = (dayOfMonth+dayShift) + intToMonth.get(month);
        timetable = timetable + String.format(dayNumberToText.get(dayOfWeek+dayShift), date);
        return timetable;
    }
}