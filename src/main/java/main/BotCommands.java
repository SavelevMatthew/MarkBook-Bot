package main;

import org.telegram.telegrambots.api.methods.ParseMode;
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

    static SendMessage SendHelp(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("Будь в курсе актуального расписания c @mrkbkbot\n" +
                "\n" +
                "✏️ ***@mrkbkbot*** — чат-бот в Telegram, заменяющий обычный дневник\n" +
                "\n" +
                "\uD83D\uDCCB Чтобы посмотреть расписание, просто нажми на кнопку ***«Расписание на сегодня»***\n" +
                "\uD83E\uDDEE Сохраняй домашние задания и смотри их в расписании\n" +
                "⚙️ В разделе ***«Настройки»*** можно отредактировать расписание и получить код, чтобы расшарить доступ к твоей группе");
        msg.setChatId((long) user.userId);
        return msg;
    }
}
