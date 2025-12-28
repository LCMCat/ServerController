package tech.ccat.controller.command;

import java.util.*;

/**
 * 命令注册器
 */
public class CommandRegistry {
    private final Map<String, Command> commands;

    public CommandRegistry() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public List<Command> getAllCommands() {
        return new ArrayList<>(commands.values());
    }
}