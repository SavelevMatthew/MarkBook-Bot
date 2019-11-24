package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;

public class BotCommands {
    public static SendMessage FromMainMenu(UserInfo user) {
        switch(user.msg_text) {
            case "Расписание на сегодня": return Timetable.getTimetable(user, 0);
            case "Расписание на завтра": return Timetable.getTimetable(user, 1);
        }
        return new SendMessage();
    }
}
