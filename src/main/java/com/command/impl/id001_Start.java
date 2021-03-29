package com.command.impl;

import com.command.Command;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id001_Start extends Command {

    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);

        return EXIT;
    }
}
