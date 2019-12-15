package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    static SendMessage FromSettings(UserInfo user) {
        switch(user.msg_text) {
            case "Добавить домашнее задание": return Hometasks.UpdateHometask(user);
            case "Редактировать расписание": return Timetable.EditTimetable(user);
            case "Добавить файл": return BotCommands.NotImplementedYet(user);
            case "Получить код группы": return Settings.GetGroupCode(user);
            case "/start": return BotCommands.ReturnToMainMenu(user);
            case "⬅️ Вернуться в главное меню": return BotCommands.ReturnToMainMenu(user);
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

        msg.setText("⚠️ Настройки доступны только администраторам групп");
        return msg;
    }

    static SendMessage GetGroupCode(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("***\uD83D\uDE4B\u200D♂️ Пригласите друзей в свою группу!\n***" +
                "\n" +
                "Попросите указать код вашей группы во время регистрации :\n" +
                String.format("```%s```", user.groupCode));
        return msg;
    }

    static List<SendMessage> SendToAllAdmins(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        switch(user.status) {
            case SETTINGS: {
                user.status = UserStatus.GET_MSG_TO_ALL_ADMINS;
                msg.setText("Введи сообщение, которое будет отправлено ***всем администраторам***");
                SQLCommands.UpdateUserInfo(user);
                return List.of(msg);
            }
            case GET_MSG_TO_ALL_ADMINS: {
                user.properties = user.msg_text;
                user.status = UserStatus.APPROVE_SENDING_MSG_TO_ALL_ADMINS;
                msg.setText("⚠️ Отправить сообщение ***всем администраторам***?");
                Keyboards.YesNo(msg);
                SQLCommands.UpdateUserInfo(user);
                return List.of(msg);
            }
            case APPROVE_SENDING_MSG_TO_ALL_ADMINS: {
                if ("❌".equals(user.msg_text)) {
                    msg.setText("\uD83D\uDC4C\uD83C\uDFFB Отменено");
                    user.status = UserStatus.SETTINGS;
                    user.properties = "";
                    SQLCommands.UpdateUserInfo(user);
                    Keyboards.Settings(user.userId, msg);
                    return List.of(msg);
                } else if ("⬅️ Вернуться в главное меню".equals(user.msg_text)) {
                    msg.setText("\uD83D\uDC4C\uD83C\uDFFB Отменено");
                    user.status = UserStatus.DEFAULT;
                    user.properties = "";
                    SQLCommands.UpdateUserInfo(user);
                    Keyboards.MainMenu(msg, user.isAdmin);
                    return List.of(msg);
                } else {
                    ArrayList<SendMessage> result = new ArrayList<>();
                    msg.setChatId((long)user.userId);
                    user.status = UserStatus.SETTINGS;
                    msg.setText("✅ Отправлено");
                    Keyboards.Settings(user.userId, msg);
                    result.add(msg);

                    for (String admin : SQLCommands.GetAdminsList()) {
                        result.add(new SendMessage(Long.parseLong(admin), "***\uD83D\uDCEC Сообщение от разработчиков\n***" + user.properties));
                        result.get(result.size() - 1).setParseMode(ParseMode.MARKDOWN);
                    }
                    SQLCommands.UpdateUserInfo(user);
                    user.properties = "";
                    return result;
                }
            }
        }
        return List.of(msg);
    }
}
