package tech.ccat.controller.command;

import java.util.List;

/**
 * 帮助命令
 */
public class HelpCommand implements Command {
    private final CommandRegistry commandRegistry;
    private final SwitchCommand switchCommand;

    public HelpCommand(CommandRegistry commandRegistry, SwitchCommand switchCommand) {
        this.commandRegistry = commandRegistry;
        this.switchCommand = switchCommand;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "显示帮助信息";
    }

    @Override
    public void execute(String[] args) {
        if (switchCommand.isInModuleView()) {
            System.out.println("模块视图模式下的可用命令:");
            System.out.println("  switch off - 退出模块视图");
            System.out.println("  exit       - 退出程序");
            return;
        }
        
        System.out.println("可用命令:");
        List<Command> commands = commandRegistry.getAllCommands();
        for (Command cmd : commands) {
            System.out.printf("  %-10s - %s%n", cmd.getName(), cmd.getDescription());
        }
        
        System.out.println("\n模块管理:");
        System.out.println("  start <模块名>        - 启动模块");
        System.out.println("  stop <模块名>         - 正常停止模块");
        System.out.println("  kill <模块名>         - 强制结束模块");
        System.out.println("  switch <模块名>       - 切换到模块输出视图");
        System.out.println("  logs <模块名> [行数]  - 查看模块日志");
        System.out.println("  status [模块名]       - 查看模块状态");
    }
}