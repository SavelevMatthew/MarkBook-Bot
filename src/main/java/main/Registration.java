package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;

public class Registration {
    public static SendMessage RegisterUser(UserInfo user) {
        switch(user.msg_text) {
                    case "/start": return StartRegistration(user);
                    case "Создать группу": return RegisterGroup(user);
                    case "Присоединиться к существующей группе": return SubscribeGroup(user);
                }
        return new SendMessage();
    }

    private static SendMessage StartRegistration(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setChatId((long) user.userId);
        msg.setText("\uD83D\uDC4B Привет! Это Маркбукбот.\n" +
                "Выбери способ регистрации:");
        Keyboards.StartRegistration(msg);
        return msg;
    }

    private static SendMessage RegisterGroup (UserInfo user) {
        return new SendMessage();
    }

    private static SendMessage SubscribeGroup (UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setText("Отправь код группы:");
        msg.setChatId((long) user.userId);
        msg.setReplyMarkup(new ReplyKeyboardRemove());
        user.status = UserStatus.GETGROUPID;
        SQLCommands.UpdateUserStatus(user);
        return msg;
    }

    public static SendMessage AddUserToGroup(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        int groupId;
        try {
            groupId = Integer.parseInt(user.msg_text);
        } catch(NumberFormatException e){
            msg.setText("Неправильный код группы. Попробуй ещё раз:");
            return msg;
        }
        String groupName = SQLCommands.GetGroupName(groupId);

        if (!groupName.equals("null")) {
            user.groupId = groupId;
            SQLCommands.UpdateUserGroup(user);
            user.status = UserStatus.DEFAULT;
            SQLCommands.UpdateUserStatus(user);
        } else {
            msg.setText("Неправильный код группы. Попробуй ещё раз:");
            return msg;
        }
        msg.setText(String.format("✅ Ты добавлен в группу %s", groupName));
        Keyboards.MainMenu(msg);
        return msg;
    }
}


