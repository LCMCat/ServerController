package tech.ccat.controller.command;

/**
 * 命令接口
 */
public interface Command {
    String getName();
    String getDescription();
    void execute(String[] args);
}