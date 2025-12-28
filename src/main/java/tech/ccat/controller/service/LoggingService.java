package tech.ccat.controller.service;

import tech.ccat.controller.core.OutputCache;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志服务
 */
public class LoggingService {
    private final Map<String, OutputCache> moduleLogs;
    private String currentModuleId;

    public LoggingService() {
        this.moduleLogs = new ConcurrentHashMap<>();
        this.currentModuleId = null;
    }

    /**
     * 记录模块输出
     */
    public void logModuleOutput(String moduleId, String line) {
        OutputCache cache = moduleLogs.computeIfAbsent(moduleId, 
            k -> new OutputCache(1000)); // 默认缓存1000行
        cache.addLine(line);
    }

    /**
     * 获取模块日志
     */
    public List<String> getModuleLogs(String moduleId, int lines) {
        OutputCache cache = moduleLogs.get(moduleId);
        if (cache == null) {
            return List.of();
        }
        return cache.getRecentLines(lines);
    }

    /**
     * 清空模块日志
     */
    public void clearModuleLogs(String moduleId) {
        OutputCache cache = moduleLogs.get(moduleId);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * 获取所有模块的日志缓存
     */
    public Map<String, OutputCache> getAllModuleLogs() {
        return new ConcurrentHashMap<>(moduleLogs);
    }

    /**
     * 设置当前选中的模块
     */
    public void setCurrentModuleId(String moduleId) {
        this.currentModuleId = moduleId;
    }

    /**
     * 获取当前选中的模块
     */
    public String getCurrentModuleId() {
        return currentModuleId;
    }

    /**
     * 检查是否应该打印模块日志到控制台
     */
    public boolean shouldPrintToConsole(String moduleId) {
        return currentModuleId != null && currentModuleId.equals(moduleId);
    }
}