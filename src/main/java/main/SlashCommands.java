package main;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;

public class SlashCommands {
    private static String helpText = "Будь в курсе актуального расписания c @mrkbkbot\n" +
            "\n" +
            "✏️ ***@mrkbkbot*** — чат-бот в Telegram, заменяющий обычный дневник\n" +
            "\n" +
            "\uD83D\uDCCB Чтобы посмотреть расписание, просто нажми на кнопку ***«Расписание на сегодня»***\n" +
            "___Если кнопка исчезла, отправь боту /start___";

    private static String startText = "\uD83D\uDC4B Привет! Это Маркбукбот, я умею присылать расписание для КН/202";

    public static synchronized SendMessage slashCommand(String chatId, String command) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(chatId);
        Keyboards.setTimetableKeyboard(sendMessage);
        switch(command) {
            case "/start": sendMessage.setText(startText); break;
            case "/help": sendMessage.setText(helpText); break;
        }
        return sendMessage;
    }
}
