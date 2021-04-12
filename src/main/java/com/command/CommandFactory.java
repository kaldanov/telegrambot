package com.command;

import com.exception.NotRealizedMethodException;
import com.command.impl.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
public class CommandFactory {

    private static Map<Integer, Class<? extends Command>> mapCommand;

    public  static Command getCommand(int id) {
        return getFormMap(id).orElseThrow(() -> new NotRealizedMethodException("Not realized for type: " + id));
    }

    private static void addCommand(Class<? extends Command> commandClass) {
        int id = -1;
        try {
            id = getId(commandClass.getName());
        }
        catch (Exception e) { log.warn("Command {} no has id, id set {}", commandClass, id); }
        if (id > 0 && mapCommand.get(id) != null)
            log.warn("ID={} has duplicate command {} - {}", id, commandClass, mapCommand.get(id));
        mapCommand.put(id, commandClass);
    }

    private static int  getId(String commandName) { return Integer.parseInt(commandName.split("_")[0].replaceAll("[^0-9]", "")); }

    private static Optional<Command> getFormMap(int id) {
        if (mapCommand == null) {
            init();
        }
        try {
            return Optional.of(mapCommand.get(id).newInstance());
        } 
        catch (Exception e) {
            log.error("Command caput: ", e);
        }
        return Optional.empty();
    }

    private static void  init() {
        mapCommand = new HashMap<>();
        addCommand(id001_Start.class);
        addCommand(id004_RequestOnay.class);
        addCommand(id003_Diplom.class);
        addCommand(id002_RequestDorms.class);
        printListCommand();
    }

    private static void                 printListCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        new TreeMap<>(mapCommand).forEach((y, x) -> stringBuilder.append(x.getSimpleName()).append("\n"));
        log.info("List command:\n{}", stringBuilder.toString());
    }
}
