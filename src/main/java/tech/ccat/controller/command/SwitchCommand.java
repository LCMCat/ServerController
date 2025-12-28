package tech.ccat.controller.command;

import tech.ccat.controller.service.ModuleManagementService;
import tech.ccat.controller.service.LoggingService;
import java.util.List;

/**
 * 切换模块输出命令
 */
public class SwitchCommand implements Command {
    private final ModuleManagementService moduleService;
    private final LoggingService loggingService;
    private String currentModuleId = null;

    public SwitchCommand(ModuleManagementService moduleService, LoggingService loggingService) {
        this.moduleService = moduleService;
        this.loggingService = loggingService;
    }

    @Override
    public String getName() {
        return "switch";
    }

    @Override
    public String getDescription() {
        return "切换到指定模块的输出视图";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            if (currentModuleId != null) {
                System.out.println("当前正在查看模块: " + currentModuleId);
                System.out.println("输入 'switch off' 退出模块视图");
            } else {
                System.out.println("用法: switch <模块名> - 切换到模块输出视图");
                System.out.println("       switch off    - 退出模块视图");
            }
            return;
        }
        
        if ("off".equals(args[1])) {
            if (currentModuleId != null) {
                System.out.println("已退出模块 " + currentModuleId + " 的输出视图");
                currentModuleId = null;
                loggingService.setCurrentModuleId(null);
            } else {
                System.out.println("当前未在任何模块输出视图中");
            }
            return;
        }
        
        String moduleId = args[1];
        if (moduleService.getModule(moduleId) == null) {
            System.out.println("模块 " + moduleId + " 不存在");
            return;
        }
        
        currentModuleId = moduleId;
        loggingService.setCurrentModuleId(moduleId);
        System.out.println("已切换到模块 " + moduleId + " 的输出视图");
        System.out.println("输入 'switch off' 退出此视图");
        
        // 显示最近的输出
        List<String> recentLogs = loggingService.getModuleLogs(moduleId, 20);
        if (!recentLogs.isEmpty()) {
            System.out.println("=== 最近输出 ===");
            recentLogs.forEach(System.out::println);
            System.out.println("=== 实时输出开始 ===");
        }
    }
    
    public boolean isInModuleView() {
        return currentModuleId != null;
    }
    
    public String getCurrentModuleId() {
        return currentModuleId;
    }
    
    public void handleRealTimeOutput(String moduleId, String outputLine) {
        if (currentModuleId != null && currentModuleId.equals(moduleId)) {
            System.out.println(outputLine);
        }
    }
}