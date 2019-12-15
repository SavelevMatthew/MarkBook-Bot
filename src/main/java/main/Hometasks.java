package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;

class Hometasks {
    static SendMessage GetAllHometasks(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("***Все домашние задания***" + Timetable.GetAllHometasks(user));
        return msg;
    }

    static SendMessage UpdateHometask(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        switch(user.status) {
            case SETTINGS: {
                msg.setText("Выбери предмет из списка:");
                Keyboards.LessonList(user, msg, true, false, false);
                user.status = UserStatus.GET_LESSON_NAME;
                SQLCommands.UpdateUserInfo(user);
                return msg;
            }
            case GET_LESSON_NAME: {
                if(" Назад".equals(user.msg_text)) {
                    msg.setText("Главное меню");
                    user.status = UserStatus.DEFAULT;
                    SQLCommands.UpdateUserInfo(user);
                    Keyboards.MainMenu(msg, user.isAdmin);
                    return msg;
                }

                msg.setText("Введи домашнее задание:");
                user.properties = user.msg_text;
                user.status = UserStatus.GET_HOMETASK;
                SQLCommands.UpdateUserInfo(user);
                msg.setReplyMarkup(new ReplyKeyboardRemove());
                return msg;
            }
            case GET_HOMETASK: {
                SQLCommands.UpdateHometask(user, user.properties, user.msg_text);
                user.status = UserStatus.DEFAULT;
                SQLCommands.UpdateUserInfo(user);
                Keyboards.MainMenu(msg, user.isAdmin);
                msg.setText(" Домашнее задание добавлено");
                return msg;
            }
        }

        return new SendMessage();
    }
}