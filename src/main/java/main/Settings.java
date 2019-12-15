package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

public class Settings {
    static SendMessage FromSettings(UserInfo user) {
        switch(user.msg_text) {
            case "Добавить домашнее задание": return Hometasks.UpdateHometask(user);
            case "Редактировать расписание": return Timetable.EditTimetable(user);
            case "Добавить файл": return BotCommands.NotImplementedYet(user);
            case "Получить код группы": return Settings.GetGroupCode(user);
            case "/start": return BotCommands.ReturnToMainMenu(user);
            case "Отправить сообщение всем администраторам": return BotCommands.NotImplementedYet(user);
            case " Вернуться в главное меню": return BotCommands.ReturnToMainMenu(user);
        }
        return new SendMessage();
    }

    static SendMessage OpenSettings(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);

        if (user.isAdmin) {
            msg.setText("Настройки");
            Keyboards.Settings(user.userId, msg);
            user.status = UserStatus.SETTINGS;
            SQLCommands.UpdateUserInfo(user);
            return msg;
        }

        msg.setText(" Настройки доступны только администраторам групп");
        return msg;
    }

    static SendMessage GetGroupCode(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("***\uD83D\uDE4B\u200D Пригласите друзей в свою группу!\n***" +
                "\n" +
                "Попросите указать код вашей группы во время регистрации :\n" +
                String.format("```%s```", user.groupCode));
        return msg;
    }
}