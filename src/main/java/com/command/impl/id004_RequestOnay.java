package com.command.impl;

import com.command.Command;
import com.entity.User;
import com.entity.custom.Onay;
import com.entity.enums.WaitingType;
import com.enums.Language;
import com.repository.OnayRepository;
import com.service.LanguageService;
import com.service.OnayReportService;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class id004_RequestOnay extends Command {
    User user;
    Onay onay;

    private int deleteId;
    private int deleteIdWrong;
    private int oldDeleteId;
    private int editId;
    private OnayReportService onayReportService = new OnayReportService();


    @Override
    public boolean execute() throws TelegramApiException {
        user = userRepository.findByChatId(chatId);
        if (!isRegistered()) {
            deleteAllMessage();
            sendMessageWithKeyboard(getText(7), 4);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteAllMessage();
                editId = sendMessageWithKeyboard(getText(18), 8);
                deleteId = sendMessageWithKeyboard(getText(8), 5);
                waitingType = WaitingType.YES_OR_NO;
                return COMEBACK;
            case YES_OR_NO:
                onay = new Onay();
                if (isButton(9)) {   //  получал
                    oldDeleteId = editId;
                    deleteAllMessage();
                    editId = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessageWithoutKeyboard();
                    deleteId = sendMessageWithKeyboard(getText(9), 6);
                    waitingType = WaitingType.SELECT_EDUCATION;
                } else if (isButton(10)) {  // не получал
                    onay.setGotOnay("Не получал");
                    oldDeleteId = editId;
                    deleteAllMessage();
                    editId = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessageWithoutKeyboard();
                    deleteId = sendMessage(getText(10));
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_EDUCATION:
                if (isButton(12)) {
                    onay.setGotOnay("Колледж/ВУЗ");
                    deleteAllMessage();
                    deleteId = sendMessage(getText(10));
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(13)) {
                    onay.setGotOnay("Школа");
                    deleteAllMessage();
                    deleteId = sendMessage(getText(10));
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(14)) {
                    oldDeleteId = editId;
                    editId = sendMessageWithKeyboard(getText(18), 8);
                    deleteAllMessage();
                    deleteAllMessageWithoutKeyboard();
                    deleteId = sendMessageWithKeyboard(getText(8), 5);
                    waitingType = WaitingType.YES_OR_NO;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_CARD_ID:
                if (isButton(14)) {
                    deleteAllMessage();
                    oldDeleteId = editId;
                    editId = sendMessageWithKeyboard(getText(18), 8);
                    deleteAllMessageWithoutKeyboard();
                    deleteId = sendMessageWithKeyboard(getText(8), 5);
                    waitingType = WaitingType.YES_OR_NO;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    if (updateMessageText.length() != 9) {
                        sendWrongData();
                    } else if (isDigit(updateMessageText)) {
                        sendWrongData();
                    } else {
                        deleteAllMessage();
                        onay.setCardId(updateMessageText);
                        deleteId = sendMessage(getText(19));
                        waitingType = WaitingType.SET_IIN;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_IIN:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(10));
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    if (updateMessageText.length() != 12) {
                        sendWrongData();
                    } else if (isDigit(updateMessageText)) {
                        sendWrongData();
                    } else {
                        deleteAllMessage();
                        onay.setIin(updateMessageText);
                        deleteId = sendMessage(getText(11));
                        waitingType = WaitingType.DATE_ISSUE;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case DATE_ISSUE:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(10));
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    try {
                        deleteAllMessage();
                        onay.setDateIssue(new SimpleDateFormat("dd.MM.yyyy").parse(updateMessageText));
                        deleteId = sendMessage(getText(12));
                        waitingType = WaitingType.DATE_END;
                    } catch (ParseException e) {
                        sendWrongData();
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case DATE_END:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(11));
                    waitingType = WaitingType.DATE_ISSUE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    Date date = null;
                    try {
                        deleteAllMessage();
                        date = new SimpleDateFormat("dd.MM.yyyy").parse(updateMessageText);
                        deleteId = sendMessage(getText(13));
                        waitingType = WaitingType.ISSUED_BY;
                        onay.setDateEnd(date);
                    } catch (ParseException e) {
                        sendWrongData();
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case ISSUED_BY:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(12));
                    waitingType = WaitingType.DATE_END;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    deleteAllMessage();
                    onay.setIssuedBy(updateMessageText);
                    deleteId = sendMessage(getText(14));
                    waitingType = WaitingType.PHOTO_CARD;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case PHOTO_CARD:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(13));
                    waitingType = WaitingType.ISSUED_BY;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasPhoto()) {
                    deleteAllMessage();
                    onay.setCardUrl(getUpdateMessagePhoto());
                    deleteId = sendMessage(getText(15));
                    waitingType = WaitingType.PHOTO;
                } else if (hasDocument()) {
                    deleteAllMessage();
                    onay.setCardUrl(getUpdateMessageFile());
                    deleteId = sendMessage(getText(15));
                    waitingType = WaitingType.PHOTO;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case PHOTO:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(14));
                    waitingType = WaitingType.PHOTO_CARD;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasPhoto()) {
                    deleteAllMessage();
                    onay.setPhotoUrl(getUpdateMessagePhoto());
                    onay.setDate(new Date());
                    onay.setFullName(user.getFullName());
                    onay.setPhone(user.getPhone());
                    onayRepository.save(onay);
                    sendMessageWithKeyboard(String.format(getText(17), user.getFullName()), 2);
                    oldDeleteId = editId;
                    deleteAllMessageWithoutKeyboard();
                    onayReportService.sendOnayReport(chatId, bot);
                } else {
                    sendWrongData();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private boolean isDigit(String s) {
        try {
            long i = Long.parseLong(s);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private void cancel() throws TelegramApiException {
        deleteAllMessage();
        deleteId = sendMessageWithKeyboard(getText(3), 2);
        deleteMessage(editId);
    }

    private void deleteAllMessageWithoutKeyboard() {
        deleteMessage(updateMessageId);
        deleteMessage(oldDeleteId);
        deleteMessage(deleteIdWrong);
    }

    private void deleteAllMessage() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteId);
        deleteMessage(deleteIdWrong);
    }

    private void sendWrongData() throws TelegramApiException {
        if (updateMessageId != 0)
            deleteMessage(updateMessageId);
        if (deleteIdWrong != 0)
            deleteMessage(deleteIdWrong);
        deleteIdWrong = sendMessage(4, chatId);

    }
}
