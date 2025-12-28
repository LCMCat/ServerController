package tech.ccat.controller.config;

import java.util.List;

/**
 * 服务器配置类
 */
public class ServerConfig {
    private int outputCacheSize;
    private int autoStartDelay;
    private List<ModuleConfig> modules;

    public ServerConfig() {
    }

    public ServerConfig(int outputCacheSize, int autoStartDelay, List<ModuleConfig> modules) {
        this.outputCacheSize = outputCacheSize;
        this.autoStartDelay = autoStartDelay;
        this.modules = modules;
    }

    public int getOutputCacheSize() {
        return outputCacheSize;
    }

    public void setOutputCacheSize(int outputCacheSize) {
        this.outputCacheSize = outputCacheSize;
    }

    public int getAutoStartDelay() {
        return autoStartDelay;
    }

    public void setAutoStartDelay(int autoStartDelay) {
        this.autoStartDelay = autoStartDelay;
    }

    public List<ModuleConfig> getModules() {
        return modules;
    }

    public void setModules(List<ModuleConfig> modules) {
        this.modules = modules;
    }
}