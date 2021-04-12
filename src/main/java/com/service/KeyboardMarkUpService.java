package com.service;

import com.entity.Button;
import com.entity.Keyboard;
import com.enums.Language;
import com.repository.ButtonRepository;
import com.repository.KeyboardRepository;
import com.repository.TelegramBotRepositoryProvider;
import com.util.Const;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class KeyboardMarkUpService {

    private ButtonRepository buttonRepository = TelegramBotRepositoryProvider.getButtonRepository();
    private KeyboardRepository keyboardRepository = TelegramBotRepositoryProvider.getKeyboardRepository();

    public Optional<ReplyKeyboard> select(long keyboardMarkUpId, long chatId) {
        if (keyboardMarkUpId < 0) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            return Optional.of(keyboard);
        }
        if (keyboardMarkUpId == 0) return Optional.empty();
        return Optional.ofNullable(getKeyboard(keyboardRepository.findById(keyboardMarkUpId), chatId));
    }

    public InlineKeyboardMarkup getInlineKeyboardMarkup(int keyboardId, long chat_id) {
        Optional<Keyboard> keyboard = keyboardRepository.findById(keyboardId);

        return keyboard.map(Keyboard::getButtonIds).map(buttonIds -> {
            String[] rows = buttonIds.split(Const.SPLIT);
            return getInlineKeyboard(rows, chat_id);
        }).orElse(null);

    }

    public ReplyKeyboard selectForEdition(long keyboardMarkUpId, Language language) {
        //System.out.println(keyboardMarkUpId);
        if (keyboardMarkUpId < 0) {
            return new ReplyKeyboardRemove();
        }
        if (keyboardMarkUpId == 0) return null;
        return getKeyboardForEdition(keyboardRepository.findById(keyboardMarkUpId), language);
    }

    private ReplyKeyboard getKeyboard(Optional<Keyboard> keyboard, long chatId) {

        return keyboard.map(Keyboard::getButtonIds).map(buttonIds -> {
            String[] rows = buttonIds.split(Const.SPLIT);
            if (keyboard.get().isInline()) {
                return getInlineKeyboard(rows, chatId);
            } else {
                return getReplyKeyboard(rows, chatId);
            }
        }).orElse(null);
    }

    private ReplyKeyboard getKeyboardForEdition(Optional<Keyboard> keyboard, Language language) {
        return keyboard.map(Keyboard::getButtonIds).map(buttonIds -> {
            String[] rows = buttonIds.split(Const.SPLIT);
            return getInlineKeyboardForEdition(rows, language);
        }).orElse(null);
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] rowIds, long chatId) {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonRepository.findByIdAndLangId(Integer.parseInt(buttonId), getLanguage(chatId).getId());
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);

                buttonText = buttonText.length() < 64 ? buttonText : buttonText.substring(0, 64);
                button.setCallbackData(buttonText);

                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private InlineKeyboardMarkup getInlineKeyboardForEdition(String[] rowIds, Language language) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonRepository.findByIdAndLangId(Integer.parseInt(buttonId), language.getId());
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                button.setCallbackData(buttonId);

                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private ReplyKeyboard getReplyKeyboard(String[] rows, long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        boolean isRequestContact = false;
        for (String buttonIdsString : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonRepository.findByIdAndLangId(Long.parseLong(buttonId), getLanguage(chatId).getId());
                KeyboardButton button = new KeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                boolean requestContact = getRequestContactKeyboard(buttonFromDb);
                button.setRequestContact(requestContact);
                if (requestContact) isRequestContact = true;
                keyboardRow.add(button);
            }
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(isRequestContact);
        return replyKeyboardMarkup;
    }

    private boolean getRequestContactKeyboard(Button button){
        return button.getId() == 8;
    }

    private Language getLanguage(long chatId) {
        if (chatId == 0) return Language.ru;
        return LanguageService.getLanguage(chatId);

    }

    public Optional<String> getButtonString(long id) {
        return keyboardRepository.findById(id).map(Keyboard::getButtonIds);
    }

    public List<Button> getListForEdit(int keyId, long chatId) {
        List<Button> list = new ArrayList<>();
        for (String x : Arrays.asList(getButtonString(keyId).map(s -> s.split(";")).orElse(null))) {
            list.add(buttonRepository.findByIdAndLangId(Integer.parseInt(x), getLanguage(chatId).getId()));
        }
        return list;

    }
}
