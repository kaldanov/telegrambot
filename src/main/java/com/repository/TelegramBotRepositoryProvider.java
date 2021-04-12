package com.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotRepositoryProvider{
//    @Getter
//    @Setter
//    private static AdminRepository adminRepository;

    @Getter
    @Setter
    private static PropertiesRepository propertiesRepository;
    @Getter
    @Setter
    private static LanguageUserRepository   languageUserRepository;
    @Getter
    @Setter
    private static SpecialityRepository   specialityRepository;
    @Getter
    @Setter
    private static ButtonRepository         buttonRepository;
    @Getter
    @Setter
    private static OnayRepository         onayRepository;
    @Getter
    @Setter
    private static MessageRepository        messageRepository;
    @Getter
    @Setter
    private static KeyboardRepository       keyboardRepository;
    @Getter
    @Setter
    private static UserRepository userRepository;


    @Autowired
    public TelegramBotRepositoryProvider
            (PropertiesRepository propertiesRepository, LanguageUserRepository languageUserRepository,
             ButtonRepository buttonRepository, OnayRepository onayRepository, MessageRepository messageRepository,
             SpecialityRepository specialityRepository,KeyboardRepository keyboardRepository, UserRepository userRepository
            ) {
        setPropertiesRepository(propertiesRepository);
        setLanguageUserRepository(languageUserRepository);
        setButtonRepository(buttonRepository);
        setMessageRepository(messageRepository);
        setKeyboardRepository(keyboardRepository);
        setOnayRepository(onayRepository);
        setSpecialityRepository(specialityRepository);
        setUserRepository(userRepository);
    }
}
