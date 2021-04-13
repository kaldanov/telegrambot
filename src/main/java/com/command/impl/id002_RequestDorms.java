package com.command.impl;

import com.command.Command;
import com.entity.User;
import com.entity.custom.Dorm;
import com.entity.custom.Onay;
import com.entity.custom.Speciality;
import com.entity.enums.Language;
import com.entity.enums.WaitingType;
import com.service.OnayReportService;
import com.util.ButtonsLeaf;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class id002_RequestDorms extends Command {
    private User user;
    private Onay onay;
    private Dorm dorm;
    private List<String> socialGroupIds;
    private ButtonsLeaf buttonsLeaf;
    private List<Speciality> specialities;
    private Map mapForMessage = new HashMap();

    private int deleteId;
    private int deleteIdWrong, deleteIdWithKeyboard;
    private int oldDeleteId;


    @Override
    public boolean execute() throws TelegramApiException {
        mapForMessage.put("54", 42);
        mapForMessage.put("55", 43);
        mapForMessage.put("56", 44);
        user = userRepository.findByChatId(chatId);
        if (!isRegistered()) {
            sendMessageWithKeyboard(getText(7), 4);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                dorm = new Dorm();
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
                    } else {
                        sendWrongData();
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
                    dorm.setEducationSchool(Integer.parseInt(updateMessageText));
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
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(5, dorm.getEducationSchool()));  //  5 - Специальность
                    deleteId = sendMessageWithKeyboard(getText(30), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(5, dorm.getEducationSchool())));
                    waitingType = WaitingType.SELECT_SPECIALITY;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    deleteAllMessage();
                    int a = 32;
                    if (updateMessageText.equals("46"))
                        a = 33;
                    dorm.setTypeDorm(Integer.parseInt(updateMessageText));
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
                    socialGroupIds = new ArrayList<>();
                    dorm.setSocialGroup("");
                    dorm.setSocialGroupUrl("");
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(8));
                    deleteId = sendMessageWithKeyboard(getText(34), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(8)));
                    waitingType = WaitingType.SELECT_SOCIAL_GROUP;
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SELECT_SOCIAL_GROUP:
                if (isButton(14)) {
                    deleteAllMessage();
                    int a = 32;
                    if (dorm.getTypeDorm() == 46)
                        a = 33;
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNamesBySchool(7, dorm.getTypeDorm()));  //  7 - Комната
                    deleteId = sendMessageWithKeyboard(getText(a), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsBySchool(7, dorm.getTypeDorm())));
                    waitingType = WaitingType.SELECT_DORM_ROOM;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasCallbackQuery()) {
                    if (specialityRepository.findAllByIdAndLangIdAndAndType(Integer.parseInt(updateMessageText), getLanguage().getId(), 8) == null)
                        sendWrongData();
                    else {
                        if (updateMessageText.equals("59")) {
                            deleteAllMessage();
                            if (socialGroupIds == null) {
                                deleteId = sendMessage(14);
                                waitingType = WaitingType.SET_CARD_PHOTO;
                                return COMEBACK;
                            } else {
                                return setAllUrlForSocialGroup();
                            }
                        } else {
                            if (socialGroupIds.size() != 4) {
                                socialGroupIds.add(updateMessageText);
                                dorm.setSocialGroup(dorm.getSocialGroup() + updateMessageText + "\n");
                                buttonsLeaf = new ButtonsLeaf(getSpecialityNamesByItselfId(8, socialGroupIds));
                                editMessageWithKeyboard(getText(34), deleteId, (InlineKeyboardMarkup) buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIdsByItselfId(8, socialGroupIds)));
                                waitingType = WaitingType.SELECT_SOCIAL_GROUP;

                            } else {
                                return setAllUrlForSocialGroup();
                            }
                        }
                    }
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_URL:
                if (isButton(14)) {
                    socialGroupIds = new ArrayList<>();
                    dorm.setSocialGroup("");
                    dorm.setSocialGroupUrl("");
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(8));
                    deleteId = sendMessageWithKeyboard(getText(34), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(8)));
                    waitingType = WaitingType.SELECT_SOCIAL_GROUP;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasDocument()) {
                    deleteAllMessage();
                    dorm.setSocialGroupUrl(dorm.getSocialGroupUrl() + updateMessageFile + "\n");
                    socialGroupIds.remove(socialGroupIds.get(0));
                    setAllUrlForSocialGroup();
                } else {
                    sendWrongData();
                }
                return COMEBACK;
            case SET_CARD_PHOTO:
                if (isButton(14)) {
                    socialGroupIds = new ArrayList<>();
                    dorm.setSocialGroup("");
                    dorm.setSocialGroupUrl("");
                    deleteAllMessage();
                    buttonsLeaf = new ButtonsLeaf(getSpecialityNames(8));
                    deleteId = sendMessageWithKeyboard(getText(34), buttonsLeaf.getListButtonWhereIdIsData(getSpecialityIds(8)));
                    waitingType = WaitingType.SELECT_SOCIAL_GROUP;
                } else if (isButton(11)) {
                    cancel();
                    return EXIT;
                } else if (hasDocument()) {
                    deleteAllMessage();
                    dorm.setCardUrl(updateMessageFile);
                    deleteAllMessage();
                    sendMessageWithKeyboard(getText(52), 2);
                } else {
                    sendWrongData();
                }
                return COMEBACK;
        }

        return EXIT;
    }

    private boolean setAllUrlForSocialGroup() throws TelegramApiException {
        if (socialGroupIds.size() != 0) {
            deleteAllMessage();
            System.out.println(mapForMessage.get(socialGroupIds.get(0)));
            if (mapForMessage.get(socialGroupIds.get(0)) != null) {
                deleteId = sendMessage(getText((Integer) mapForMessage.get(socialGroupIds.get(0))));
                waitingType = WaitingType.SET_URL;
                return COMEBACK;
            } else {
                deleteId = sendMessage(14);
                waitingType = WaitingType.SET_CARD_PHOTO;
                return COMEBACK;
            }
        } else {
            deleteId = sendMessage(14);
            waitingType = WaitingType.SET_CARD_PHOTO;
            return COMEBACK;
        }
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

    private List<String> getSpecialityIdsByItselfId(int type, List<String> ids) {
        specialities = specialityRepository.findAllByLangIdAndTypeOrderById(getLanguage().getId(), type);
        List<String> specialityIds = new ArrayList<>();
        for (Speciality speciality : specialities) {
            if (!ids.contains(String.valueOf(speciality.getId())))
                specialityIds.add(String.valueOf(speciality.getId()));
        }
        return specialityIds;
    }

    private List<String> getSpecialityNamesByItselfId(int type, List<String> ids) {
        specialities = specialityRepository.findAllByLangIdAndTypeOrderById(getLanguage().getId(), type);
        List<String> specialityNames = new ArrayList<>();
        for (Speciality speciality : specialities) {
            if (!ids.contains(String.valueOf(speciality.getId())))
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
