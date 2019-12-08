package main;

import org.telegram.telegrambots.api.methods.send.SendMessage;

class BotCommands {
    static SendMessage FromMainMenu(UserInfo user) {
        switch(user.msg_text) {
            case "Расписание на сегодня": return Timetable.getTimetable(user, 0);
            case "Расписание на завтра": return Timetable.getTimetable(user, 1);
            case "Добавить домашнее задание": return Hometasks.UpdateHometask(user);
        }
        return new SendMessage();
    }
}
