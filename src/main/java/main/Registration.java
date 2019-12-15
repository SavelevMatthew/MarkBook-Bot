package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;

class Registration {
    static SendMessage RegisterUser(UserInfo user) {
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

    static SendMessage RegisterGroup(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long) user.userId);
        if(user.status == UserStatus.HELLO) {
            msg.setText("Отправь название группы:");
            msg.setReplyMarkup(new ReplyKeyboardRemove());
            user.status = UserStatus.GET_GROUPNAME;
            SQLCommands.UpdateUserInfo(user);
            return msg;
        }
        String groupName = user.msg_text;
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setText(String.format("***✅ Группа %s создана***\nЗаполняем раписание группы на ***Понедельник***.\n\nОтправь номер первой пары:", groupName));
        user.status = UserStatus.GET_FIRSTLESSON_NUMBER;
        user.properties = "Понедельник";
        SQLCommands.CreateNewGroup(user, groupName);
        return msg;
    }

    static SendMessage SetFirstLesson(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setChatId((long)user.userId);
        if("✅ Завершить заполнение расписания".equals(user.msg_text))
        {
            user.status = UserStatus.DEFAULT;
            SQLCommands.UpdateUserInfo(user);
            Keyboards.MainMenu(msg, user.isAdmin);
            msg.setText("\uD83D\uDC4C\uD83C\uDFFB Регистрация завершена");
            return msg;
        }

        int lessonNumber;
        try {
            lessonNumber = Integer.parseInt(user.msg_text);
        } catch(NumberFormatException a) {
            msg.setText("***⚠️Номер первой пары — это число от 1 до 7***\nОтправь номер первой пары ещё раз:");
            return  msg;
        }

        if (lessonNumber > 7) {
            msg.setText("***⚠️Номер первой пары — это число от 1 до 7***\nОтправь номер первой пары ещё раз:");
            return  msg;
        }

        user.status = UserStatus.valueOf(String.format("GET_LESSON%d", lessonNumber));
        SQLCommands.InitTimetableDay(user, user.properties, user.msg_text);
        SQLCommands.UpdateUserInfo(user);
        msg.setText("Выбери название из списка или введи новое\nв формате эмодзи+название:\n___\uD83D\uDCBB Объектно-ориентированное программирование___\n\n***После ввода последней пары нажми ✅ Готово.*** Бот перейдет к заполнению расписания на следующий день");
        Keyboards.LessonList(user, msg, false, true, true);
        return msg;
    }

    static SendMessage UpdateTimetable(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        if ("✅ Готово".equals(user.msg_text)) {
            SwitchUpdateTimetableToNextDay(user, msg);
            return msg;
        }
        int lessonNumber = 0;
        switch(user.status) {
            case GET_LESSON1: lessonNumber = 1; user.status = UserStatus.GET_LESSON2; break;
            case GET_LESSON2: lessonNumber = 2; user.status = UserStatus.GET_LESSON3; break;
            case GET_LESSON3: lessonNumber = 3; user.status = UserStatus.GET_LESSON4; break;
            case GET_LESSON4: lessonNumber = 4; user.status = UserStatus.GET_LESSON5; break;
            case GET_LESSON5: lessonNumber = 5; user.status = UserStatus.GET_LESSON6; break;
            case GET_LESSON6: lessonNumber = 6; user.status = UserStatus.GET_LESSON7; break;
            case GET_LESSON7: lessonNumber = 7; break;
        }

        AddLessonIfNotExists(user);

        SQLCommands.UpdateTimetable(user, user.properties, lessonNumber, user.msg_text);
        if(lessonNumber == 7) {
            SwitchUpdateTimetableToNextDay(user, msg);
            return msg;
        }

        msg.setText("Урок добавлен. Отправь следующий:");
        SQLCommands.UpdateUserInfo(user);
        Keyboards.LessonList(user, msg, false, true, true);
        return msg;
    }

    private static void SwitchUpdateTimetableToNextDay(UserInfo user, SendMessage msg) {
        if("Воскресенье".equals(user.properties)) {
            user.msg_text = "✅ Завершить заполнение расписания";
            msg = SetFirstLesson(user);
        }

        user.status = UserStatus.GET_FIRSTLESSON_NUMBER;
        switch(user.properties) {
            case "Понедельник": user.properties = "Вторник"; break;
            case "Вторник": user.properties = "Среда"; break;
            case "Среда": user.properties = "Четверг"; break;
            case "Четверг": user.properties = "Пятница"; break;
            case "Пятница": user.properties = "Суббота"; break;
            case "Суббота": user.properties = "Воскресенье"; break;
        }

        SQLCommands.UpdateUserInfo(user);
        msg.setText(String.format("Заполняем раписание группы на ***%s***.\n\nОтправь номер первой пары:\n", user.properties));
        Keyboards.EndRegistration(msg);
    }

    private static void AddLessonIfNotExists(UserInfo user) {
        if(!"\uD83E\uDD37\u200D♂️ Окно".equals(user.msg_text) && !SQLCommands.GetLessonList(user).contains(user.msg_text)) {
            SQLCommands.AddLesson(user, user.msg_text);
        }
    }

    private static SendMessage SubscribeGroup (UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setText("Отправь код группы:");
        msg.setChatId((long) user.userId);
        msg.setReplyMarkup(new ReplyKeyboardRemove());
        user.status = UserStatus.GET_GROUPID;
        SQLCommands.UpdateUserInfo(user);
        return msg;
    }

    static SendMessage AddUserToGroup(UserInfo user) {
        SendMessage msg = new SendMessage();
        msg.setChatId((long)user.userId);
        String groupCode = user.msg_text;

        String groupInfo = SQLCommands.GetGroupName(groupCode);
        if ("-1 ".equals(groupInfo)) {
            msg.setText("Неправильный код группы. Попробуй ещё раз:");
            return msg;
        }

        String[] splitted = groupInfo.split("!");
        user.groupId = Integer.parseInt(splitted[0]);
        user.groupCode = user.msg_text;
        user.status = UserStatus.DEFAULT;
        String groupName = splitted[1];

        msg.setText(String.format("✅ Ты добавлен в группу %s", groupName));
        Keyboards.MainMenu(msg, user.isAdmin);
        SQLCommands.UpdateUserInfo(user);
        return msg;
    }
}


