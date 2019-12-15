package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

class BotCommands {
    static SendMessage FromMainMenu(UserInfo user) {
        switch(user.msg_text) {
            case "Расписание на сегодня": return Timetable.getTimetable(user, 0);
            case "Расписание на завтра": return Timetable.getTimetable(user, 1);
            case "Все домашние задания": return Hometasks.GetAllHometasks(user);
            case "Настройки": return Settings.OpenSettings(user);
            case "/start": return ReturnToMainMenu(user);
        }
        return new SendMessage();
    }

    static SendMessage SendHelp(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("***Будь в курсе актуального расписания c @mrkbkbot\n***" +
                        "\n" +
                        "✏️ ***@mrkbkbot*** — чат-бот в Telegram, заменяющий обычный дневник\n" +
                        "\n" +
                        "\uD83D\uDCCB Чтобы посмотреть расписание, просто нажми на кнопку ***«Расписание на сегодня»***\n" +
                        "\uD83E\uDDEE Сохраняй домашние задания и смотри их в расписании\n" +
                        "⚙️ В разделе ***«Настройки»*** можно отредактировать расписание и получить код, чтобы расшарить доступ к твоей группе\n" +
                        "\n" +
                        "___Проблемы с ботом? Отправь запрос разработчикам командой /support___");
        msg.setChatId((long) user.userId);
        return msg;
    }

    static SendMessage CallToSupport(UserInfo user, String username) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText(String.format("***\uD83D\uDED1 Проблема у @%s:***\n", username) + user.msg_text);
        msg.setChatId((long)409216737);
        String[] splitted = user.properties.split("|");
        user.status = UserStatus.valueOf(splitted[0]);
        user.properties = splitted[1];
        System.out.print(user.properties);
        SQLCommands.UpdateUserInfo(user);
        return msg;
    }

    static SendMessage InitCallToSupport(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setText("Отправь текст запроса в службу поддержки");
        user.properties = user.status.toString() + '|' + user.properties;
        user.status = UserStatus.GET_SUPPORT_TEXT;
        SQLCommands.UpdateUserInfo(user);
        return msg;
    }

    static SendMessage ReturnToMainMenu(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        user.status = UserStatus.DEFAULT;
        msg.setText("Главное меню");
        Keyboards.MainMenu(msg, user.isAdmin);
        SQLCommands.UpdateUserInfo(user);
        return msg;
    }

    static SendMessage NotImplementedYet(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText("\uD83E\uDD37\u200D♂️ Эта функция пока не реализована, но скоро появится");
        msg.setChatId((long) user.userId);
        return msg;
    }
}
