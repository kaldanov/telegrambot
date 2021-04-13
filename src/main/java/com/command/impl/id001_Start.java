package com.command.impl;

import com.command.Command;
import com.entity.User;
import com.entity.enums.WaitingType;
import com.enums.Language;
import com.service.LanguageService;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id001_Start extends Command {
    private User user = new User();
    private int deleteId;

    @Override
    public boolean execute() throws TelegramApiException {

        switch (waitingType) {
            case START:
                deleteAllMessage();
                if (isButton(2)) {
                    LanguageService.setLanguage(chatId, Language.kz);
                    return registrationOption();
                }
                else if (isButton(3)) {
                    LanguageService.setLanguage(chatId, Language.ru);
                    return registrationOption();
                }
                else deleteId = sendMessageWithKeyboard(getText(1), 1);
                return COMEBACK;
            case SET_FULL_NAME:
                if (hasMessageText()) {
                    user.setFullName(updateMessageText);
                    deleteAllMessage();
                    sendMessageWithKeyboard(getText(5), 3);
                    waitingType = WaitingType.SET_PHONE;
                } else sendWrongData();
                return COMEBACK;
            case SET_PHONE:
                if (update.getMessage().hasContact()) {
                    String phone = update.getMessage().getContact().getPhoneNumber();

                    if (phone.charAt(0) == '8') {
                        phone = phone.replaceFirst("8", "+7");
                    } else if (phone.charAt(0) == '7') {
                        phone = phone.replaceFirst("7", "+7");
                    }
                    user.setPhone(phone);
                    registrationUser(user);
                    sendMessageWithKeyboard(getText(3), 2);
                    return EXIT;
                } else sendWrongData();
                return COMEBACK;

        }
        return EXIT;
    }

    private void registrationUser(User user) {
        user.setChatId(chatId);
        userRepository.save(user);
    }

    private boolean registrationOption() throws TelegramApiException {
        deleteAllMessage();
        if (isRegistered()) {
            deleteId = sendMessageWithKeyboard(getText(3), 2);
            return EXIT;
        } else {
            deleteId = sendMessage(getText(2), chatId);
            deleteKeyboard();
            waitingType = WaitingType.SET_FULL_NAME;
            return COMEBACK;
        }
    }

    private void deleteAllMessage() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteId);
    }

    private void sendWrongData() throws TelegramApiException {
        sendMessage(4, chatId);

    }
}
