package com.service;

import com.command.Command;
import com.command.CommandFactory;
import com.entity.Button;
import com.entity.User;
import com.enums.Language;
import com.exception.CommandNotFoundException;
import com.repository.ButtonRepository;
import com.repository.TelegramBotRepositoryProvider;
import com.repository.UserRepository;
import com.util.Const;
import com.util.UpdateUtil;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service
public class CommandService {

    private long                chatId;
    private ButtonRepository    buttonRepository = TelegramBotRepositoryProvider.getButtonRepository();
    private UserRepository      userRepository   = TelegramBotRepositoryProvider.getUserRepository();

    public      Optional<Command>   getCommand(Update update)               throws CommandNotFoundException {
        chatId                  = UpdateUtil.getChatId(update);
        Message updateMessage   = update.getMessage();
        String  inputtedText;
        if (update.hasCallbackQuery()) {
            inputtedText        = update.getCallbackQuery().getData().split(Const.SPLIT)[0];
            updateMessage       = update.getCallbackQuery().getMessage();
            try {
                if (inputtedText != null && inputtedText.substring(0, 6).equals(Const.ID_MARK)) {
                    try {
                        return Optional.ofNullable(getCommandById(Integer.parseInt(inputtedText.split(Const.SPLIT)[0].replaceAll(Const.ID_MARK, ""))));
                    } catch (Exception e) { inputtedText = updateMessage.getText(); }
                }
            } catch (Exception e) {}
        } else {
            try {
                inputtedText = updateMessage.getText();
            } catch (Exception e) { throw new CommandNotFoundException("No data is available"); }
        }
        try {

            return getCommand(buttonRepository.findByName(inputtedText));
        } catch (IncorrectResultSizeDataAccessException | javax.persistence.NonUniqueResultException | org.hibernate.NonUniqueResultException e){
            User user;
            if (userRepository.findByChatId(chatId)!=null){
                user = userRepository.findByChatId(chatId);
            } else user = new User();
            return getCommand(buttonRepository.findByName(inputtedText));
        }
    }

    public      Optional<Command>   getCommand(Optional<Button> button)     throws CommandNotFoundException {
        return button.map(Button::getCommandId).map(integer -> {
            return Optional.ofNullable(CommandFactory.getCommand(integer)).map(command -> {
                command.setId(integer);
                command.setMessageId(button.map(Button::getMessageId).orElse(0));
                return command;
            });
        }).orElseThrow(() -> new CommandNotFoundException("No data is available"));
    }

    private     Command             getCommandById(int id) { return CommandFactory.getCommand(id); }

    protected   Language            getLanguage() {
        if (chatId == 0) return Language.ru;
        return LanguageService.getLanguage(chatId);
    }
}
