package tech.ccat.controller.service;

import tech.ccat.controller.config.ModuleConfig;
import tech.ccat.controller.config.ServerConfig;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自动启动服务
 */
public class AutoStartService {
    private final ModuleManagementService moduleService;
    private final ServerConfig serverConfig;
    private final ScheduledExecutorService scheduler;

    public AutoStartService(ModuleManagementService moduleService, ServerConfig serverConfig) {
        this.moduleService = moduleService;
        this.serverConfig = serverConfig;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * 初始化自动启动
     */
    public void init() {
        if (serverConfig.getModules() == null) {
            return;
        }

        for (ModuleConfig moduleConfig : serverConfig.getModules()) {
            if (moduleConfig.isAutoStart()) {
                scheduleAutoStart(moduleConfig);
            }
        }
    }

    /**
     * 调度自动启动
     */
    private void scheduleAutoStart(ModuleConfig config) {
        int delay = config.getAutoStartDelay() > 0 ? config.getAutoStartDelay() : 
                   serverConfig.getAutoStartDelay();

        scheduler.schedule(() -> {
            System.out.println("自动启动模块: " + config.getName());
            moduleService.startModule(config.getName());
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}