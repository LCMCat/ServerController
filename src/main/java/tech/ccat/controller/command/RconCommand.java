package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;
import tech.ccat.controller.service.LoggingService;

/**
 * Rcon命令 - 向当前切换到的模块发送命令
 */
public class RconCommand implements Command {
    private final ModuleManagementService moduleService;
    private final LoggingService loggingService;

    public RconCommand(ModuleManagementService moduleService, LoggingService loggingService) {
        this.moduleService = moduleService;
        this.loggingService = loggingService;
    }

    @Override
    public String getName() {
        return "r";
    }

    @Override
    public String getDescription() {
        return "向当前切换到的模块发送命令（rcon）";
    }

    @Override
    public void execute(String[] args) {
        // 检查是否有当前选中的模块
        String currentModuleId = loggingService.getCurrentModuleId();
        if (currentModuleId == null) {
            System.out.println("错误：当前未切换到任何模块");
            System.out.println("请先使用 'switch <模块名>' 切换到目标模块");
            return;
        }

        // 检查参数
        if (args.length < 2) {
            System.out.println("用法: r <命令> - 向当前模块发送命令");
            System.out.println("示例: r op CMCat - 向当前模块发送 'op CMCat' 命令");
            return;
        }

        // 构建命令字符串（跳过第一个参数 "r"）
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                commandBuilder.append(" ");
            }
            commandBuilder.append(args[i]);
        }
        String command = commandBuilder.toString();

        // 检查模块是否存在且正在运行
        if (moduleService.getModule(currentModuleId) == null) {
            System.out.println("错误：模块 " + currentModuleId + " 不存在");
            return;
        }

        // 发送命令到模块
        boolean success = moduleService.sendCommandToModule(currentModuleId, command);
        if (success) {
            System.out.println("已向模块 " + currentModuleId + " 发送命令: " + command);
        } else {
            System.out.println("错误：向模块 " + currentModuleId + " 发送命令失败");
            System.out.println("请确保模块正在运行");
        }
    }
}