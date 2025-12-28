package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;
import tech.ccat.controller.core.Module;
import java.util.Map;

/**
 * 状态命令
 */
public class StatusCommand implements Command {
    private final ModuleManagementService moduleService;

    public StatusCommand(ModuleManagementService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "查看模块状态";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            // 显示所有模块状态
            System.out.println("=== 所有模块状态 ===");
            Map<String, Module> modules = moduleService.getAllModules();
            if (modules.isEmpty()) {
                System.out.println("没有注册的模块");
                return;
            }
            
            System.out.printf("%-20s %-10s %-15s %s%n", 
                "模块名", "状态", "启动时间", "重启次数");
            System.out.println("-".repeat(60));
            
            for (Module module : modules.values()) {
                String startTime = module.getStartTime() != null ? 
                    module.getStartTime().toString() : "-";
                System.out.printf("%-20s %-10s %-15s %d%n", 
                    module.getId(), 
                    module.getState(), 
                    startTime.length() > 15 ? startTime.substring(0, 15) : startTime,
                    module.getRestartCount());
            }
        } else {
            // 显示指定模块详细状态
            String moduleId = args[1];
            Module module = moduleService.getModule(moduleId);
            if (module == null) {
                System.out.println("模块 " + moduleId + " 不存在");
                return;
            }
            
            System.out.println("=== 模块 " + moduleId + " 详细状态 ===");
            System.out.println("状态: " + module.getState());
            System.out.println("启动时间: " + (module.getStartTime() != null ? 
                module.getStartTime() : "-"));
            System.out.println("重启次数: " + module.getRestartCount());
            System.out.println("输出缓存大小: " + module.getOutputCache().size());
        }
    }
}