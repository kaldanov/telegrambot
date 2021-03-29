package com.service;

import com.entity.LanguageUser;
import com.enums.Language;
import com.repository.LanguageUserRepository;
import com.repository.TelegramBotRepositoryProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LanguageService {

    private static Map<Long, Language>      languageMap         = new HashMap<>();
    private static LanguageUserRepository   languageUserRepo    = TelegramBotRepositoryProvider.getLanguageUserRepository();

    public  static  Language    getLanguage(long chatId) {
        return Optional.ofNullable(languageMap.get(chatId)).orElseGet(() -> languageUserRepo.findByChatId(chatId).map(languageUser -> {
            Language language = Language.getById(languageUser.getLanguageId());
            languageMap.put(chatId, language);
            return language;
        }).orElseGet(() -> { return setLanguage(chatId, Language.ru); }));
    }

    public  static  Language    setLanguage(long chatId, Language language) {
        languageMap.put(chatId, language);
        return Language.getById(languageUserRepo.save(languageUserRepo.findByChatId(chatId).orElse(new LanguageUser()).setLanguageId(language.getId()).setChatId(chatId)).getLanguageId());
    }
}
