package com.config;

import com.command.Command;
import com.enums.Language;
import com.exception.CommandNotFoundException;
import com.repository.KeyboardRepository;
import com.repository.MessageRepository;
import com.repository.TelegramBotRepositoryProvider;
import com.service.CommandService;
import com.service.KeyboardMarkUpService;
import com.service.LanguageService;
import com.util.DateUtil;
import com.util.SetDeleteMessages;
import com.util.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;

@Slf4j
public class Conversation {

    private Long chatId;

    private long currentChatId;
    public Command command;
    private CommandService commandService = new CommandService();
    private String empty = "";

    private MessageRepository messageRepository = TelegramBotRepositoryProvider.getMessageRepository();
    private KeyboardRepository keyboardRepository = TelegramBotRepositoryProvider.getKeyboardRepository();
    protected KeyboardMarkUpService keyboardMarkUpService = new KeyboardMarkUpService();

    public void handleUpdate(Update update, DefaultAbsSender bot) throws TelegramApiException {
        printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        checkLanguage(chatId);
        try {
            command = commandService.getCommand(update).map(command1 -> {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
                return command1;
            }).orElse(null);

        } catch (CommandNotFoundException e) {
            if (chatId < 0) return;
            if (command == null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
                ReplyKeyboard replyKeyboard = keyboardMarkUpService.select(1, chatId)
                        .orElseThrow(() -> new TelegramApiException("keyboard id" + 1 + "not found"));
                bot.execute(new SendMessage().setChatId(chatId).setText(messageRepository.findByIdAndLangId(1, getLanguage().getId()).getName()).setReplyMarkup(replyKeyboard));
            }
        }
        if (command != null) {
            if (command.isInitNormal(update, bot)) {
                clear();
                return;
            }
            boolean commandFinished = command.execute();
            if (commandFinished) clear();
        }
    }

    private void printUpdate(Update update) {
        String dataMessage = empty;
        if (update.hasMessage())
            dataMessage = DateUtil.getDbMmYyyyHhMmSs(new Date((long) update.getMessage().getDate() * 1000));
        log.info("New update get {} -> send response {}", dataMessage, DateUtil.getDbMmYyyyHhMmSs(new Date()));
        log.info(UpdateUtil.toString(update));
    }


    private void checkLanguage(long chatId) {
        LanguageService.getLanguage(chatId);
    }

    private Language getLanguage() {
        if (chatId == 0) return Language.ru;
        return LanguageService.getLanguage(chatId);
    }

    private void clear() {
        command.clear();
        command = null;
    }
}
