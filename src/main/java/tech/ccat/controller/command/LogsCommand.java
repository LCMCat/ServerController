package tech.ccat.controller.command;

import tech.ccat.controller.service.LoggingService;
import java.util.List;

/**
 * 查看日志命令
 */
public class LogsCommand implements Command {
    private final LoggingService loggingService;

    public LogsCommand(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public String getName() {
        return "logs";
    }

    @Override
    public String getDescription() {
        return "查看模块日志";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("用法: logs <模块名> [行数]");
            return;
        }
        
        String moduleId = args[1];
        int lines = args.length > 2 ? Integer.parseInt(args[2]) : 50;
        
        List<String> logs = loggingService.getModuleLogs(moduleId, lines);
        if (logs.isEmpty()) {
            System.out.println("模块 " + moduleId + " 没有日志记录");
            return;
        }
        
        System.out.println("=== 模块 " + moduleId + " 最近 " + logs.size() + " 行日志 ===");
        logs.forEach(System.out::println);
        System.out.println("=== 日志结束 ===");
    }
}