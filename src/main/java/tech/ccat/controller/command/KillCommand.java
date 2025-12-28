package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;

/**
 * 强制结束命令
 */
public class KillCommand implements Command {
    private final ModuleManagementService moduleService;

    public KillCommand(ModuleManagementService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public String getDescription() {
        return "强制结束指定模块进程";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: kill <模块名>");
            return;
        }
        
        String moduleId = args[1];
        boolean success = moduleService.killModule(moduleId);
        if (success) {
            System.out.println("模块 " + moduleId + " 已强制结束");
        } else {
            System.out.println("强制结束模块 " + moduleId + " 失败");
        }
    }
}