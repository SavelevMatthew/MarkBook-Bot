import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

public class TextCommands {
    public static synchronized SendMessage getTimetabletext(String chatId, int dayShift) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        sendMessage.setText(Timetable.createTimetable(dayShift));
        Keyboards.setTimetableKeyboard(sendMessage);
        return sendMessage;
    }
}
