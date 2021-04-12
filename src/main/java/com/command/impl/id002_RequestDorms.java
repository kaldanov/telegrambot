package com.command.impl;

import com.command.Command;
import com.entity.User;
import com.entity.custom.Onay;
import com.entity.custom.Speciality;
import com.entity.enums.Language;
import com.entity.enums.WaitingType;
import com.service.OnayReportService;
import com.util.ButtonsLeaf;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id002_RequestDorms extends Command {
    User user;
    Onay onay;
    private ButtonsLeaf buttonsLeaf;
    private List<Speciality> specialities;

    private int deleteId;
    private int deleteIdWrong, deleteIdWithKeyboard;
    private int oldDeleteId;
    private int prev;


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
                deleteIdWithKeyboard = sendMessageWithKeyboard(getText(18), 8);
                deleteId = sendMessageWithKeyboard(getText(20), 10);
                waitingType = WaitingType.SELECT_GENDER;
                return COMEBACK;
            case SELECT_GENDER:
                if (isButton(20)) {  //Men
                    oldDeleteId = deleteIdWithKeyboard;
                    deleteIdWithKeyboard = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessage();
                    deleteId = sendMessage(getText(21));
                    waitingType = WaitingType.SELECT_BIRTH_DATE;
                } else if (isButton(21)) {  //Women
                    oldDeleteId = deleteIdWithKeyboard;
                    deleteIdWithKeyboard = sendMessageWithKeyboard(getText(18), 7);
                    deleteAllMessage();
                    deleteId = sendMessage(getText(21));
                    waitingType = WaitingType.SELECT_BIRTH_DATE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_BIRTH_DATE:
                if (isButton(14)) {
                    oldDeleteId = deleteIdWithKeyboard;
                    deleteIdWithKeyboard = sendMessageWithKeyboard(getText(18), 8);
                    deleteAllMessage();
                    deleteId = sendMessageWithKeyboard(getText(20), 10);
                    waitingType = WaitingType.SELECT_GENDER;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    try {
                        new SimpleDateFormat("dd.MM.yyyy").parse(updateMessageText);
                        deleteAllMessage();
                        deleteId = sendMessage(getText(26));
                        waitingType = WaitingType.SELECT_NATIONALITY;
                    } catch (ParseException e) {
                        sendWrongData();
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_NATIONALITY:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(21));
                    waitingType = WaitingType.SELECT_BIRTH_DATE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(19));
                    waitingType = WaitingType.SET_IIN;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_IIN:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(21));
                    waitingType = WaitingType.SELECT_BIRTH_DATE;
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
                        deleteId = sendMessage(getText(22));
                        waitingType = WaitingType.SELECT_ADDRESS;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_ADDRESS:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(19));
                    waitingType = WaitingType.SET_IIN;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(23));
                    waitingType = WaitingType.SET_PHONE;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_PHONE:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(22));
                    waitingType = WaitingType.SELECT_ADDRESS;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    if (parseNumber(updateMessageText).length() == 12) {
                        String phone = parseNumber(updateMessageText);
                        deleteAllMessage();
                        deleteId = sendMessage(getText(24));
                        waitingType = WaitingType.SET_PHONE_PARENT;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_PHONE_PARENT:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(23));
                    waitingType = WaitingType.SET_PHONE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasMessageText()) {
                    if (parseNumber(updateMessageText).length() == 12) {
                        String phone = parseNumber(updateMessageText);
                        deleteAllMessage();
                        buttonsLeaf = new ButtonsLeaf(getSpecialityNames(1));  //  1 - Язык обучения
                        deleteId = sendMessageWithKeyboard(getText(25), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(1)));
                        waitingType = WaitingType.SET_LANG;
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_LANG:
                if (isButton(14)) {
                    deleteAllMessage();
                    deleteId = sendMessage(getText(24));
                    waitingType = WaitingType.SET_PHONE_PARENT;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(2));  //  2 - Курс обучения
                    deleteId = sendMessageWithKeyboard(getText(27), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(2)));
                    waitingType = WaitingType.SELECT_COURSE;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_COURSE:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(1));  //  1 - Язык обучения
                    deleteId = sendMessageWithKeyboard(getText(25), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(1)));
                    waitingType = WaitingType.SET_LANG;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(3));  //  3 - Срок обучения
                    deleteId = sendMessageWithKeyboard(getText(29), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(3)));
                    waitingType = WaitingType.SELECT_COURSE_PERIOD;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_COURSE_PERIOD:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(2));  //  2 - Курс обучения
                    deleteId = sendMessageWithKeyboard(getText(27), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(2)));
                    waitingType = WaitingType.SELECT_COURSE;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(4));  //  4 - Школа
                    deleteId = sendMessageWithKeyboard(getText(28), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(4)));
                    waitingType = WaitingType.SELECT_SCHOOL;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_SCHOOL:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(3));  //  3 - Срок обучения
                    deleteId = sendMessageWithKeyboard(getText(29), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(3)));
                    waitingType = WaitingType.SELECT_COURSE_PERIOD;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    prev = Integer.parseInt(updateMessageText);
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(5, Integer.parseInt(updateMessageText)));  //  5 - Специальность
                    deleteId = sendMessageWithKeyboard(getText(30), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(5, Integer.parseInt(updateMessageText))));
                    waitingType = WaitingType.SELECT_SPECIALITY;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_SPECIALITY:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(4));  //  4 - Школа
                    deleteId = sendMessageWithKeyboard(getText(28), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(4)));
                    waitingType = WaitingType.SELECT_SCHOOL;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(6));   // 6 - Общяга
                    deleteId = sendMessageWithKeyboard(getText(31), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(6)));
                    waitingType = WaitingType.SELECT_DORM;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_DORM:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(5, prev));  //  5 - Специальность
                    deleteId = sendMessageWithKeyboard(getText(30), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(5, prev)));
                    waitingType = WaitingType.SELECT_SPECIALITY;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    int a = 32;
                    if (updateMessageText.equals("46"))
                        a = 33;
                    prev = Integer.parseInt(updateMessageText);
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(7, Integer.parseInt(updateMessageText)));  //  7 - Комната
                    deleteId = sendMessageWithKeyboard(getText(a), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(7, Integer.parseInt(updateMessageText))));
                    waitingType = WaitingType.SELECT_DORM_ROOM;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_DORM_ROOM:
                if (isButton(14)) {
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(6));   // 6 - Общяга
                    deleteId = sendMessageWithKeyboard(getText(31), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(6)));
                    waitingType = WaitingType.SELECT_DORM;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    deleteId = sendMessageWithKeyboard(getText(34),11);
                    waitingType = WaitingType.SELECT_SOCIAL_GROUP;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_SOCIAL_GROUP:
                if (isButton(14)) {
//                    deleteAllMessage();
//                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(7, Integer.parseInt(updateMessageText)));  //  7 - Комната
//                    deleteId = sendMessageWithKeyboard(getText(a), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(7, Integer.parseInt(updateMessageText))));
//                    waitingType = WaitingType.SELECT_DORM_ROOM;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (isButton(11)) {
                    deleteAllMessage();
                    deleteId = sendMessageWithKeyboard(getText(34),11);
                    waitingType = WaitingType.SELECT_DORM;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
        }

        return EXIT;
    }

    private List<String> getSpecialityIdsBySchool(int type, int schoolId) {
        specialities = specialityRepository.findAllByLangIdAndTypeAndSchoolType(getLanguage().getId(), type, schoolId);
        List<String> specialityIds = new ArrayList<>();
        for (Speciality speciality : specialities) {
            specialityIds.add(String.valueOf(speciality.getId()));
        }
        return specialityIds;
    }

    private List<String> getSpecialityNamesBySchool(int type, int schoolId) {
        specialities = specialityRepository.findAllByLangIdAndTypeAndSchoolType(getLanguage().getId(), type, schoolId);
        List<String> specialityNames = new ArrayList<>();
        for (Speciality speciality : specialities) {
            specialityNames.add(speciality.getName());
        }
        return specialityNames;
    }

    private List<String> getSpecialityNames(int type) {
        specialities = specialityRepository.findAllByLangIdAndTypeOrderById(getLanguage().getId(), type);
        List<String> specialityNames = new ArrayList<>();
        for (Speciality speciality : specialities) {
            specialityNames.add(speciality.getName());
        }
        return specialityNames;
    }

    private List<String> getSpecialityIds(int type) {
        specialities = specialityRepository.findAllByLangIdAndTypeOrderById(getLanguage().getId(), type);
        List<String> specialityIds = new ArrayList<>();
        for (Speciality speciality : specialities) {
            specialityIds.add(String.valueOf(speciality.getId()));
        }
        return specialityIds;
    }

    private String parseNumber(String updateMessageText) {
        if (updateMessageText.charAt(0) == '8') {
            updateMessageText = updateMessageText.replaceFirst("8", "+7");
        } else if (updateMessageText.charAt(0) == '7') {
            updateMessageText = updateMessageText.replaceFirst("7", "+7");
        }
        return updateMessageText;
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
        sendMessageWithKeyboard(getText(3), 2);
        deleteMessage(updateMessageId);
        deleteMessage(deleteId);
        deleteMessage(deleteIdWithKeyboard);
    }

    private void deleteAllMessageWithoutKeyboard() {
        deleteMessage(updateMessageId);
        deleteMessage(oldDeleteId);
        deleteMessage(deleteIdWrong);
        deleteMessage(deleteId);
    }

    private void deleteAllMessage() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteId);
        deleteMessage(deleteIdWrong);
        deleteMessage(oldDeleteId);
    }

    private void deleteSendMessages() {
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
