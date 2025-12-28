package tech.ccat.controller.core;

import tech.ccat.controller.config.ModuleConfig;
import java.time.Instant;

/**
 * 模块实体类
 */
public class Module {
    private final String id;
    private final ModuleConfig config;
    private Process process;
    private ModuleState state;
    private final OutputCache outputCache;
    private Instant startTime;
    private int restartCount;

    public Module(String id, ModuleConfig config) {
        this.id = id;
        this.config = config;
        this.state = ModuleState.STOPPED;
        this.outputCache = new OutputCache(config.getOutputCacheSize());
        this.restartCount = 0;
    }

    public String getId() {
        return id;
    }

    public ModuleConfig getConfig() {
        return config;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ModuleState getState() {
        return state;
    }

    public void setState(ModuleState state) {
        this.state = state;
    }

    public OutputCache getOutputCache() {
        return outputCache;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public int getRestartCount() {
        return restartCount;
    }

    public void incrementRestartCount() {
        this.restartCount++;
    }

    public void resetRestartCount() {
        this.restartCount = 0;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        // 清空输出缓存
        if (outputCache != null) {
            outputCache.clear();
        }
        
        // 清理进程引用
        if (process != null) {
            process.destroyForcibly();
            process = null;
        }
        
        // 重置状态
        state = ModuleState.STOPPED;
        startTime = null;
    }
}