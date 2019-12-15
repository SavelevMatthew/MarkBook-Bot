package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

class CallbackHandler {
    static EditMessageText StartHandling(CallbackQuery callbackQuery, UserInfo user) {
        String data = callbackQuery.getData();
        switch(data) {
            case "hw": return EditTimetableToHometask(callbackQuery, user);
            case "tt": return EditHometaskToTimetable(callbackQuery, user);
        }
        return new EditMessageText();
    }

    private static EditMessageText EditHometaskToTimetable(CallbackQuery callbackQuery, UserInfo user) {
        Message message = callbackQuery.getMessage();
        EditMessageText editMsg = new EditMessageText();
        editMsg.setMessageId(message.getMessageId());
        editMsg.setChatId(message.getChatId());
        editMsg.setParseMode(ParseMode.MARKDOWN);
        editMsg.setText(Timetable.ReturnTimetableInEdited(user, message));
        editMsg.setReplyMarkup(Keyboards.InlineButton(callbackQuery));
        return editMsg;
    }

    private static EditMessageText EditTimetableToHometask(CallbackQuery callbackQuery, UserInfo user) {
        Message message = callbackQuery.getMessage();
        EditMessageText editMsg = new EditMessageText();
        editMsg.setChatId(message.getChatId());
        editMsg.setMessageId(message.getMessageId());
        editMsg.setParseMode(ParseMode.MARKDOWN);
        editMsg.setText(Timetable.GetHometaskByDay(user, message));
        editMsg.setReplyMarkup(Keyboards.InlineButton(callbackQuery));
        return editMsg;
    }
}