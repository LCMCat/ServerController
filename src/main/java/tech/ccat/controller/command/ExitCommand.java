package tech.ccat.controller.command;

import tech.ccat.controller.ServerController;

/**
 * 退出命令
 */
public class ExitCommand implements Command {
    private final ServerController controller;

    public ExitCommand(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "退出程序";
    }

    @Override
    public void execute(String[] args) {
        System.out.println("正在停止所有模块...");
        controller.shutdown();
        System.out.println("程序退出");
        System.exit(0);
    }
}