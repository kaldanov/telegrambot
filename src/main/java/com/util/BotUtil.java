package com.util;

import com.entity.Message;
import com.enums.Language;
import com.enums.ParseMode;
import com.repository.MessageRepository;
import com.repository.TelegramBotRepositoryProvider;
import com.service.KeyboardMarkUpService;
import com.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class BotUtil {

    private DefaultAbsSender bot;
    private MessageRepository messageRepository     = TelegramBotRepositoryProvider.getMessageRepository();
    private long                    chatId;
//    private KeyboardMarkUpService   keyboardMarkUpService;

    public BotUtil(DefaultAbsSender bot) { this.bot = bot; }

    public  int  sendMessage(SendMessage sendMessage)  throws TelegramApiException {
        //System.out.println(sendMessage);
        //System.out.println(sendMessage);
        try {
            //System.out.println("WORKED");
            //int id = bot.execute(sendMessage).getMessageId();
            //System.out.println(id);
            return bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(sendMessage.getText() + "\nBad tags");
                return bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    public  int         sendMessage(String text, long chatId)                                                           throws TelegramApiException { return sendMessage(text, chatId, ParseMode.html); }

    public  int         sendMessage(String text, long chatId, ParseMode parseMode)                                      throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(text);
        if (parseMode == ParseMode.WITHOUT) {
            sendMessage.setParseMode(null);
        } else {
            sendMessage.setParseMode(parseMode.name());
        }
        return sendMessage(sendMessage);
    }

    public  int         sendMessage(long messageId, long chatId)  throws TelegramApiException {
        return sendMessage(messageId, chatId, null, null);
    }

    public  int         sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {

        int result                          = 0;
        this.chatId                         = chatId;

        Message message                     = messageRepository.findByIdAndLangId((int)messageId, getLanguage().getId());
        SendMessage sendMessage             = new SendMessage().setText(message.getName()).setChatId(chatId).setParseMode(ParseMode.html.name());
        KeyboardMarkUpService keyboardMarkUpService = new KeyboardMarkUpService();

        if (message.getKeyboardId() != null){
            sendMessage.setReplyMarkup(keyboardMarkUpService.select(message.getKeyboardId(), chatId).get());
        }

        AtomicBoolean isCaption             = new AtomicBoolean(false);
        if (photo != null) {
            SendPhoto sendPhoto             = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(photo);
            if (message.getName().length() < 200) {
                sendPhoto.setCaption(message.getName());
                isCaption.set(true);
            }
            try {
                result                      = bot.execute(sendPhoto).getMessageId();
            } catch (TelegramApiException e) {
                log.debug("Can't send photo", e);
                isCaption.set(false);
            }
        }
        if (!isCaption.get()) result        = bot.execute(sendMessage).getMessageId();
        if (contact != null) sendContact(chatId, contact);
        return result;
    }

    public  int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboard, chatId, 0);
    }

    private int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId, int replyMessageId)   throws TelegramApiException {
        //System.out.println(text+" === "+keyboard+" === "+chatId+" === "+replyMessageId);
        //System.out.println(keyboard);
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.html.name()).setChatId(chatId).setText(text).setReplyMarkup(keyboard);

        //System.out.println(sendMessage);
        if (replyMessageId != 0) sendMessage.setReplyToMessageId(replyMessageId);
        //System.out.println(sendMessage);
        return sendMessage(sendMessage);
    }

    public int sendMessageWithPhotoAndKeyboard(String text, ReplyKeyboard keyboard, long chatId,String photo )   throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto().setChatId(chatId).setPhoto(photo).setCaption(text).setReplyMarkup(keyboard).setParseMode("html");

        return bot.execute(sendPhoto).getMessageId();

    }

    public  int         sendContact(long chatId, Contact contact)                                                       throws TelegramApiException { return bot.execute(new SendContact().setChatId(chatId).setFirstName(contact.getFirstName()).setLastName(contact.getLastName()).setPhoneNumber(contact.getPhoneNumber())).getMessageId(); }

    public  void        deleteMessage(long chatId, int messageId) {
        try {
            bot.execute(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {}
    }

    public  boolean     hasContactOwner(Update update) { return (update.hasMessage() && update.getMessage().hasContact()) && Objects.equals(update.getMessage().getFrom().getId(), update.getMessage().getContact().getUserID()); }

    private Language    getLanguage() {
        if (chatId == 0) {
            return Language.ru;
        }
        return LanguageService.getLanguage(chatId);
    }

}
