package tech.ccat.controller.service;

import tech.ccat.controller.config.ModuleConfig;
import tech.ccat.controller.core.ModuleProcess;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程管理器
 */
public class ProcessManager {
    private final Map<String, ModuleProcess> processes;
    private final LoggingService loggingService;

    public ProcessManager(LoggingService loggingService) {
        this.processes = new ConcurrentHashMap<>();
        this.loggingService = loggingService;
    }

    /**
     * 启动进程
     */
    public ModuleProcess startProcess(String moduleId, ModuleConfig config) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd.exe", "/c", config.getStartCommand());
        } else {
            processBuilder.command("sh", "-c", config.getStartCommand());
        }
        
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        ModuleProcess moduleProcess = new ModuleProcess(process, moduleId, loggingService);
        processes.put(moduleId, moduleProcess);
        
        return moduleProcess;
    }

    /**
     * 停止进程
     */
    public boolean stopProcess(String moduleId, boolean force) {
        ModuleProcess moduleProcess = processes.get(moduleId);
        if (moduleProcess == null) {
            return false;
        }

        try {
            if (force) {
                moduleProcess.getProcess().destroyForcibly();
            } else {
                moduleProcess.getProcess().destroy();
            }
            
            // 清理资源
            moduleProcess.cleanup();
            processes.remove(moduleId);
            return true;
        } catch (Exception e) {
            // 即使失败也尝试清理资源
            try {
                moduleProcess.cleanup();
            } catch (Exception cleanupEx) {
                System.err.println("清理资源失败: " + cleanupEx.getMessage());
            }
            processes.remove(moduleId);
            return false;
        }
    }

    /**
     * 强制结束进程
     */
    public boolean killProcess(String moduleId) {
        ModuleProcess moduleProcess = processes.get(moduleId);
        if (moduleProcess == null) {
            return false;
        }

        try {
            moduleProcess.getProcess().destroyForcibly();
            boolean terminated = moduleProcess.getProcess().waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            
            // 清理资源
            moduleProcess.cleanup();
            processes.remove(moduleId);
            
            return terminated;
        } catch (Exception e) {
            // 即使失败也尝试清理资源
            try {
                moduleProcess.cleanup();
            } catch (Exception cleanupEx) {
                System.err.println("清理资源失败: " + cleanupEx.getMessage());
            }
            processes.remove(moduleId);
            return false;
        }
    }

    /**
     * 获取进程信息
     */
    public ModuleProcess getProcess(String moduleId) {
        return processes.get(moduleId);
    }

    /**
     * 检查进程是否存活
     */
    public boolean isProcessAlive(String moduleId) {
        ModuleProcess moduleProcess = processes.get(moduleId);
        return moduleProcess != null && moduleProcess.getProcess().isAlive();
    }

    /**
     * 停止所有进程
     */
    public void stopAllProcesses() {
        for (String moduleId : processes.keySet()) {
            stopProcess(moduleId, true);
        }
        processes.clear();
    }
}