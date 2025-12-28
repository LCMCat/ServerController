package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;

/**
 * 停止模块命令
 */
public class StopCommand implements Command {
    private final ModuleManagementService moduleService;

    public StopCommand(ModuleManagementService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "停止指定模块";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: stop <模块名> [force]");
            return;
        }
        
        String moduleId = args[1];
        boolean force = args.length > 2 && "force".equals(args[2]);
        boolean success = moduleService.stopModule(moduleId, force);
        if (success) {
            System.out.println("模块 " + moduleId + " 停止中...");
        }
    }
}