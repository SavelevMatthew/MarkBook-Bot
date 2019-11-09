import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    private static Logger log = Logger.getLogger(Bot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();

        switch (message) {
            case "Расписание на сегодня":   sendMessage = TextCommands.getTimetabletext(chatId, 0);
                                            break;
            case "Расписание на завтра":    sendMessage = TextCommands.getTimetabletext(chatId, 1);
                                            break;
            case "/start":                  sendMessage = SlashCommands.slashCommand(chatId, message);
                                            break;
            case "/help":                   sendMessage = SlashCommands.slashCommand(chatId, message);
                                            break;
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
                log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        return "mrkbkbot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}
