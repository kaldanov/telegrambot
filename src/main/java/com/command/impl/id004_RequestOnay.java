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
            sendMessageWithKeyboard(getText(7), 4);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteAllMessage();
                deleteId = sendMessageWithKeyboard(getText(18), 8);
                editId = sendMessageWithKeyboard(getText(8), 5);
                waitingType = WaitingType.YES_OR_NO;
                return COMEBACK;
            case YES_OR_NO:
                onay = new Onay();
                if (isButton(9)) {
                    oldDeleteId = deleteId;
                    deleteMessage(editId);
                    deleteId = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessageWithoutKeyboard();
                    editId = sendMessageWithKeyboard(getText(9), 6);
                    waitingType = WaitingType.SELECT_EDUCATION;
                } else if (isButton(10)) {
                    onay.setGotOnay("Не получал");
                    oldDeleteId = deleteId;
                    deleteMessage(editId);
                    deleteId = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessageWithoutKeyboard();
                    editId = sendMessage(getText(10));
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
                    editMessage(getText(10), editId);
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(13)) {
                    onay.setGotOnay("Школа");
                    editMessage(getText(10), editId);
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(14)) {
                    oldDeleteId = deleteId;
                    deleteId = sendMessageWithKeyboard(getText(18), 8);
                    deleteMessage(editId);
                    editId = sendMessageWithKeyboard(getText(8), 5);
                    deleteAllMessageWithoutKeyboard();
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
                    oldDeleteId = deleteId;
                    deleteId = sendMessageWithKeyboard(getText(18), 8);
                    deleteMessage(editId);
                    editId = sendMessageWithKeyboard(getText(8), 5);
                    deleteAllMessageWithoutKeyboard();
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
                        deleteMessage(updateMessageId);
                        onay.setCardId(updateMessageText);
                        editMessage(getText(19), editId);
                        waitingType = WaitingType.SET_IIN;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_IIN:
                if (isButton(14)) {
                    deleteMessage(updateMessageId);
                    editMessage(getText(10), editId);
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
                        deleteMessage(updateMessageId);
                        onay.setIin(updateMessageText);
                        editMessage(getText(11), editId);
                        waitingType = WaitingType.DATE_ISSUE;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case DATE_ISSUE:
                if (isButton(14)) {
                    deleteMessage(updateMessageId);
                    editMessage(getText(10), editId);
                    waitingType = WaitingType.SET_CARD_ID;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    try {
                        deleteMessage(updateMessageId);
                        onay.setDateIssue(new SimpleDateFormat("dd.MM.yyyy").parse(updateMessageText));
                        editMessage(getText(12), editId);
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
                    deleteMessage(updateMessageId);
                    editMessage(getText(11), editId);
                    waitingType = WaitingType.DATE_ISSUE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    Date date = null;
                    try {
                        deleteMessage(updateMessageId);
                        date = new SimpleDateFormat("dd.MM.yyyy").parse(updateMessageText);
                        editMessage(getText(13), editId);
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
                    deleteMessage(updateMessageId);
                    editMessage(getText(12), editId);
                    waitingType = WaitingType.DATE_END;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    deleteMessage(updateMessageId);
                    onay.setIssuedBy(updateMessageText);
                    editMessage(getText(14), editId);
                    waitingType = WaitingType.PHOTO_CARD;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case PHOTO_CARD:
                if (isButton(14)) {
                    deleteMessage(updateMessageId);
                    editMessage(getText(13), editId);
                    waitingType = WaitingType.ISSUED_BY;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasPhoto()) {
                    deleteMessage(updateMessageId);
                    onay.setCardUrl(getUpdateMessagePhoto());
                    editMessage(getText(15), editId);
                    waitingType = WaitingType.PHOTO;
                } else if (hasDocument()) {
                    deleteMessage(updateMessageId);
                    onay.setCardUrl(getUpdateMessageFile());
                    editMessage(getText(15), editId);
                    waitingType = WaitingType.PHOTO;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case PHOTO:
                if (isButton(14)) {
                    deleteMessage(updateMessageId);
                    editMessage(getText(14), editId);
                    waitingType = WaitingType.PHOTO_CARD;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasPhoto()) {
                    deleteMessage(updateMessageId);
                    onay.setPhotoUrl(getUpdateMessagePhoto());
                    onay.setDate(new Date());
                    onay.setFullName(user.getFullName());
                    onay.setPhone(user.getPhone());
                    onayRepository.save(onay);
                    deleteMessage(editId);
                    sendMessageWithKeyboard(String.format(getText(17), user.getFullName()), 2);
                    onayReportService.sendOnayReport(chatId,bot);
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
        deleteMessage(editId);
        deleteId = sendMessageWithKeyboard(getText(3), 2);
        deleteAllMessageWithoutKeyboard();
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
