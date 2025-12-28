package tech.ccat.controller.service;

import tech.ccat.controller.config.ModuleConfig;
import tech.ccat.controller.core.Module;
import tech.ccat.controller.core.ModuleState;
import tech.ccat.controller.core.ModuleProcess;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块管理服务
 */
public class ModuleManagementService {
    private final Map<String, Module> modules;
    private final ProcessManager processManager;

    public ModuleManagementService(LoggingService loggingService) {
        this.modules = new ConcurrentHashMap<>();
        this.processManager = new ProcessManager(loggingService);
    }

    /**
     * 注册模块
     */
    public void registerModule(String moduleId, ModuleConfig config) {
        Module module = new Module(moduleId, config);
        modules.put(moduleId, module);
        System.out.println("模块 " + moduleId + " 已注册");
    }

    /**
     * 启动模块
     */
    public boolean startModule(String moduleId) {
        Module module = modules.get(moduleId);
        if (module == null) {
            System.out.println("模块 " + moduleId + " 不存在");
            return false;
        }

        if (module.getState() == ModuleState.RUNNING || module.getState() == ModuleState.STARTING) {
            System.out.println("模块 " + moduleId + " 已经在运行或启动中");
            return false;
        }

        module.setState(ModuleState.STARTING);
        
        try {
            ModuleProcess process = processManager.startProcess(moduleId, module.getConfig());
            module.setProcess(process.getProcess());
            module.setStartTime(Instant.now());
            module.setState(ModuleState.RUNNING);
            
            System.out.println("模块 " + moduleId + " 启动成功");
            return true;
        } catch (IOException e) {
            module.setState(ModuleState.ERROR);
            System.err.println("启动模块 " + moduleId + " 失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 停止模块
     */
    public boolean stopModule(String moduleId, boolean force) {
        Module module = modules.get(moduleId);
        if (module == null) {
            System.out.println("模块 " + moduleId + " 不存在");
            return false;
        }

        if (module.getState() != ModuleState.RUNNING) {
            System.out.println("模块 " + moduleId + " 未在运行");
            return false;
        }

        module.setState(ModuleState.STOPPING);
        
        // 尝试软停止
        if (!force && module.getConfig().getStopCommand() != null && 
            !module.getConfig().getStopCommand().trim().isEmpty()) {
            try {
                ModuleProcess process = processManager.getProcess(moduleId);
                if (process != null) {
                    process.sendCommand(module.getConfig().getStopCommand());
                    
                    // 等待进程正常退出
                    for (int i = 0; i < 10; i++) {
                        if (!processManager.isProcessAlive(moduleId)) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("软停止模块 " + moduleId + " 失败: " + e.getMessage());
            }
        }

        // 强制停止
        boolean success = processManager.stopProcess(moduleId, force);
        if (success) {
            module.setState(ModuleState.STOPPED);
            module.setProcess(null);
            System.out.println("模块 " + moduleId + " 已停止");
        } else {
            module.setState(ModuleState.ERROR);
            module.setProcess(null);
            System.err.println("停止模块 " + moduleId + " 失败");
        }

        return success;
    }

    /**
     * 强制结束模块
     */
    public boolean killModule(String moduleId) {
        Module module = modules.get(moduleId);
        if (module == null) {
            System.out.println("模块 " + moduleId + " 不存在");
            return false;
        }

        boolean success = processManager.killProcess(moduleId);
        if (success) {
            module.setState(ModuleState.STOPPED);
            module.setProcess(null);
            System.out.println("模块 " + moduleId + " 已强制结束");
        } else {
            module.setState(ModuleState.ERROR);
            module.setProcess(null);
            System.err.println("强制结束模块 " + moduleId + " 失败");
        }

        return success;
    }

    /**
     * 获取模块状态
     */
    public ModuleState getModuleState(String moduleId) {
        Module module = modules.get(moduleId);
        return module != null ? module.getState() : null;
    }

    /**
     * 获取所有模块
     */
    public Map<String, Module> getAllModules() {
        return new ConcurrentHashMap<>(modules);
    }

    /**
     * 获取模块信息
     */
    public Module getModule(String moduleId) {
        return modules.get(moduleId);
    }

    /**
     * 停止所有模块
     */
    public void stopAllModules() {
        for (String moduleId : modules.keySet()) {
            stopModule(moduleId, true);
        }
    }

    /**
     * 向模块发送命令
     */
    public boolean sendCommandToModule(String moduleId, String command) {
        ModuleProcess process = processManager.getProcess(moduleId);
        if (process == null) {
            System.out.println("模块 " + moduleId + " 未在运行");
            return false;
        }

        process.sendCommand(command);
        return true;
    }
}