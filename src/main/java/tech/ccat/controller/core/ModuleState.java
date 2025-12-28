package tech.ccat.controller.core;

/**
 * 模块状态枚举
 */
public enum ModuleState {
    STOPPED,      // 已停止
    STARTING,     // 启动中
    RUNNING,      // 运行中
    STOPPING,     // 停止中
    ERROR,        // 错误状态
    RESTARTING    // 重启中
}