package com.config;

import com.util.UpdateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@Getter
@Setter
public class Bot extends TelegramLongPollingBot {
    @Value("${telegram-bot-username}")
    private String                  botUsername;

    @Value("${telegram-bot-token}")
    private String                  botToken;
    private Map<Long, Conversation> conversations = new HashMap<>();

    @PostConstruct
    public  void            initIt() throws TelegramApiRequestException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(this);
        log.info("Bot was registered : " + botUsername);
    }
    @Override
    public void onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (TelegramApiException e) {
            log.error("Error in conversation handleUpdate" + e);
        }
    }
    private Conversation    getConversation(Update update) {
        Long chatId                 = UpdateUtil.getChatId(update);
        Conversation conversation   = conversations.get(chatId);
        if (conversation == null) {
            log.info("InitNormal new conversation for '{}'", chatId);
            conversation            = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }
}
