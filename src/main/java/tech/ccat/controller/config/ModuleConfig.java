package tech.ccat.controller.config;

/**
 * 模块配置类
 */
public class ModuleConfig {
    private String name;
    private String startCommand;
    private String stopCommand;
    private boolean autoStart;
    private int autoStartDelay;
    private boolean autoRestart;
    private int outputCacheSize;

    public ModuleConfig() {
    }

    public ModuleConfig(String name, String startCommand, String stopCommand, 
                       boolean autoStart, int autoStartDelay, boolean autoRestart, 
                       int outputCacheSize) {
        this.name = name;
        this.startCommand = startCommand;
        this.stopCommand = stopCommand;
        this.autoStart = autoStart;
        this.autoStartDelay = autoStartDelay;
        this.autoRestart = autoRestart;
        this.outputCacheSize = outputCacheSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    public String getStopCommand() {
        return stopCommand;
    }

    public void setStopCommand(String stopCommand) {
        this.stopCommand = stopCommand;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public int getAutoStartDelay() {
        return autoStartDelay;
    }

    public void setAutoStartDelay(int autoStartDelay) {
        this.autoStartDelay = autoStartDelay;
    }

    public boolean isAutoRestart() {
        return autoRestart;
    }

    public void setAutoRestart(boolean autoRestart) {
        this.autoRestart = autoRestart;
    }

    public int getOutputCacheSize() {
        return outputCacheSize;
    }

    public void setOutputCacheSize(int outputCacheSize) {
        this.outputCacheSize = outputCacheSize;
    }
}