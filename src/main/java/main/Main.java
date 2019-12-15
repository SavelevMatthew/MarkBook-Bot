package main;

import org.telegram.telegrambots.*;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.Calendar;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}