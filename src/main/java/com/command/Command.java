package com.command;

import com.entity.Keyboard;
import com.entity.enums.WaitingType;
import com.enums.Language;
import com.repository.*;
import com.service.KeyboardMarkUpService;
import com.service.LanguageService;
import com.util.BotUtil;
import com.util.SetDeleteMessages;
import com.util.UpdateUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Optional;

@Slf4j
@Data
public abstract class Command {

    @Getter
    protected long id;
    @Getter
    protected int messageId;
    protected Update update;
    protected DefaultAbsSender bot;
    protected WaitingType waitingType = WaitingType.START;
    protected KeyboardMarkUpService keyboardMarkUpService = new KeyboardMarkUpService();
    protected final static boolean EXIT = true;
    protected final static boolean COMEBACK = false;
    protected static BotUtil botUtils;
    protected long chatId;
    protected Message updateMessage;
    protected String updateMessageText;
    protected int updateMessageId;
    protected String editableTextOfMessage;
    protected String updateMessagePhoto;
    protected String updateMessageFile;
    protected String updateMessagePhone;
    protected int lastSendMessageID;

    protected static final String               next                        = "\n";
    protected static final String               space                       = " ";
    protected              String               nextButton                  = ">>";
    protected              String               prevButton                  = "<<";


    protected boolean isExitFromBot;
    protected MessageRepository messageRepository = TelegramBotRepositoryProvider.getMessageRepository();
    protected ButtonRepository buttonRepository = TelegramBotRepositoryProvider.getButtonRepository();
    protected OnayRepository onayRepository = TelegramBotRepositoryProvider.getOnayRepository();
    protected UserRepository userRepository = TelegramBotRepositoryProvider.getUserRepository();
    protected LanguageUserRepository languageUserRepository = TelegramBotRepositoryProvider.getLanguageUserRepository();
    protected KeyboardRepository keyboardRepository    = TelegramBotRepositoryProvider.getKeyboardRepository();
    protected PropertiesRepository propertiesRepository = TelegramBotRepositoryProvider.getPropertiesRepository();
    protected SpecialityRepository specialityRepository = TelegramBotRepositoryProvider.getSpecialityRepository();

    public abstract boolean execute() throws TelegramApiException;

    protected int sendMessageWithKeyboard(int messageId, ReplyKeyboard keyboard) throws TelegramApiException {
        return sendMessageWithKeyboard(getText(messageId)  , keyboard);
    }

    protected void editMessage(String text, long chatId, int messageId) throws TelegramApiException {
        botUtils.editMessage(text, chatId, messageId);
    }
    protected void editMessage(String text, int messageId) throws TelegramApiException {
        botUtils.editMessage(text, chatId, messageId);
    }

    protected void editMessageWithKeyboard(String text, int messageId, InlineKeyboardMarkup inlineKeyboardMarkup) throws TelegramApiException {
        botUtils.editMessageWithKeyboard(text, chatId, messageId, inlineKeyboardMarkup);
    }
    protected void editMessageWithKeyboard(String text,long chatId, int messageId, InlineKeyboardMarkup inlineKeyboardMarkup) throws TelegramApiException {
        botUtils.editMessageWithKeyboard(text, chatId, messageId, inlineKeyboardMarkup);
    }

    protected void editMessageWithKeyboard(String text,long chatId, int messageId, int keyboardId) throws TelegramApiException {
        botUtils.editMessageWithKeyboard(text, chatId, messageId, keyboardMarkUpService.getInlineKeyboardMarkup(keyboardId, chatId));
    }

    protected void editMessageWithKeyboard(String text, int messageId, int keyboardId) throws TelegramApiException {
        botUtils.editMessageWithKeyboard(text, chatId, messageId, keyboardMarkUpService.getInlineKeyboardMarkup(keyboardId, chatId));
    }

    protected int sendMessageWithKeyboard(String text, int keyboardId) throws TelegramApiException,NullPointerException {

        ReplyKeyboard replyKeyboard = keyboardMarkUpService.select(keyboardId ,chatId)
                .orElseThrow(() -> new TelegramApiException("keyboard id" + keyboardId + "not found"));

        return sendMessageWithKeyboard(text, replyKeyboard);
    }

    protected int sendMessageWithPhotoAndKeyboard(String text, long keyboardId, String photo) throws TelegramApiException,NullPointerException {

        Optional<String> btn = Optional.of("");
        if (keyboardId == 5){
             btn = keyboardRepository.findById(keyboardId).map(Keyboard::getButtonIds);
        }
        ReplyKeyboard replyKeyboard = keyboardMarkUpService.select(keyboardId, chatId)
                .orElseThrow(() -> new TelegramApiException("keyboard id" + keyboardId + "not found"));

        return botUtils.sendMessageWithPhotoAndKeyboard(text,replyKeyboard,chatId,photo);
    }

    protected int sendMessageWithPhotoAndKeyboard(String text, int keyboardId, String photo, long chatId) throws TelegramApiException,NullPointerException {

        Optional<String> btn = Optional.of("");
        if (keyboardId == 5){
             btn = keyboardRepository.findById((Integer) keyboardId).map(Keyboard::getButtonIds);
        }
        ReplyKeyboard replyKeyboard = keyboardMarkUpService.select(keyboardId, chatId)
                .orElseThrow(() -> new TelegramApiException("keyboard id" + keyboardId + "not found"));

        return botUtils.sendMessageWithPhotoAndKeyboard(text,replyKeyboard,chatId,photo);
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard) throws TelegramApiException {

        lastSendMessageID = sendMessageWithKeyboard(text, keyboard, chatId);
        return lastSendMessageID;
    }

    protected int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return botUtils.sendMessageWithKeyboard(text, keyboard, chatId);
    }

    protected int sendMessage(String text) throws TelegramApiException {
        return sendMessage(text, chatId);
    }

    protected int sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, null);
    }

    protected int sendMessage(String text, long chatId, Contact contact) throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(text, chatId);
        if (contact != null) botUtils.sendContact(chatId, contact);
        return lastSendMessageID;
    }

    protected int sendMessage(long messageId) throws TelegramApiException {
        return sendMessage(messageId, chatId);
    }

    protected int sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null);
    }
    private void sendMessPhotoWithTextAndWithKeyboard(String photo, String text, int keyboardId) throws TelegramApiException {
        sendMessageWithPhotoAndKeyboard(text,keyboardId,photo);
    }
    protected int sendMessage(long messageId, long chatId, Contact contact) throws TelegramApiException {
        return sendMessage(messageId, chatId, contact, null);
    }

    protected int sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(messageId, chatId, contact, photo);
        return lastSendMessageID;
    }

    protected int toDeleteKeyboard(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int sendMessageWithKeyboardTest(String text, ReplyKeyboard keyboard, long chatID) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.HTML).setChatId(chatID).setText(text).setReplyMarkup(keyboard);
        sendMessageTest(text, sendMessage);
        return lastSendMessageID;
    }

    protected int toDeleteMessage(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    public void clear() {
        update = null;
        bot = null;
    }

    protected void deleteMessage(int messageId) {
        deleteMessage(chatId, messageId);
    }

    protected void deleteMessage(long chatId, int messageId) {
        botUtils.deleteMessage(chatId, messageId);
    }

    private void sendMessageTest(String text, SendMessage sendMessage) throws TelegramApiException {
        try {
            lastSendMessageID = bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(text + next + "Wrong number");
                lastSendMessageID = bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }





    protected String getLinkForUser(long chatId, String userName) {
        return String.format("<a href = \"tg://user?id=%s\">%s</a>", chatId, userName);
    }

    protected String getText(int messageIdFromDb) {
        String name = null;
        try {
             name = messageRepository.findByIdAndLangId(messageIdFromDb, getLanguage().getId()).getName();//messageRepository.getMessageText(messageIdFromDb, getLanguage().getId());//.orElseThrow(() -> new Exception("Message not found " + messageIdFromDb));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return name;
    }

    public boolean isInitNormal(Update update, DefaultAbsSender bot) throws TelegramApiException {
        if (botUtils == null) botUtils = new BotUtil(bot);
        this.update = update;
        this.bot = bot;
        chatId = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            updateMessageId = updateMessage.getMessageId();
            editableTextOfMessage = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage = update.getMessage();
            updateMessageId = updateMessage.getMessageId();
            if (updateMessage.hasText()) updateMessageText = updateMessage.getText();
            if (updateMessage.hasPhoto()) {
                int size = update.getMessage().getPhoto().size();
                updateMessagePhoto = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto = null;
            }
            if(updateMessage.hasDocument()){
                updateMessageFile = update.getMessage().getDocument().getFileId();
            }
        }
        if (hasContact()) updateMessagePhone = update.getMessage().getContact().getPhoneNumber();

        return false;
    }


    protected boolean isRegistered() {
        //System.out.println(userRepository.countByChatId(chatId));
        return userRepository.countByChatId(chatId) >= 0 && userRepository.countByChatId(chatId) != 0;
    }

//    protected boolean isAdmin(){
//        return adminRepository.findByUserId(chatId) != null;
//    }


    protected boolean isUser(){
        return userRepository.countByChatId(chatId) > 0;
    }


    protected boolean hasContact() {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    protected boolean isButton(int buttonId) {
        return updateMessageText.equals(buttonRepository.getButtonText(buttonId,  getLanguage().getId()));
    }

    protected boolean hasCallbackQuery() {
        return update.hasCallbackQuery();
    }

    protected boolean hasMessageText() {
        return update.hasMessage() && update.getMessage().hasText();
    }

    protected boolean hasPhoto() {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    protected boolean hasDocument() {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    protected boolean hasAudio() {
        return update.hasMessage() && update.getMessage().getAudio() != null;
    }

    protected boolean hasVideo() {
        return update.hasMessage() && update.getMessage().getVideo() != null;
    }

    protected String getBolt(String s) {
        return "<b>" + s + "</b>";
    }

    protected void deleteKeyboard() throws TelegramApiException {
        int deleteMessageId = sendMessageWithKeyboard("...", 1);
        deleteMessage(deleteMessageId);
    }
    protected Language getLanguage() {
        if (chatId == 0) return Language.ru;
        return LanguageService.getLanguage(chatId);
    }
    protected Language getLanguage(long chatIdOfUser) {
        if (chatIdOfUser == 0) return Language.ru;
        return LanguageService.getLanguage(chatIdOfUser);
    }

}
