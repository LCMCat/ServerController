package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;

/**
 * 启动模块命令
 */
public class StartCommand implements Command {
    private final ModuleManagementService moduleService;

    public StartCommand(ModuleManagementService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "启动指定模块";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: start <模块名>");
            return;
        }
        
        String moduleId = args[1];
        boolean success = moduleService.startModule(moduleId);
        if (success) {
            System.out.println("模块 " + moduleId + " 启动中...");
        }
    }
}