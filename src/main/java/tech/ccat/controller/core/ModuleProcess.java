package tech.ccat.controller.core;

import tech.ccat.controller.service.LoggingService;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模块进程包装类
 */
public class ModuleProcess {
    private Process process;
    private Thread outputReader;
    private Thread errorReader;
    private OutputStream stdin;
    private String moduleId;
    private ExecutorService executor;
    private LoggingService loggingService;

    public ModuleProcess(Process process, String moduleId, LoggingService loggingService) {
        this.process = process;
        this.moduleId = moduleId;
        this.loggingService = loggingService;
        this.stdin = process.getOutputStream();
        this.executor = Executors.newCachedThreadPool();
        
        startOutputReaders();
    }

    private void startOutputReaders() {
        // 标准输出读取器
        outputReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 记录到日志服务
                    if (loggingService != null) {
                        loggingService.logModuleOutput(moduleId, line);
                        // 只在当前模块被选中时才输出到控制台
                        if (loggingService.shouldPrintToConsole(moduleId)) {
                            System.out.println("[" + moduleId + "] " + line);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("模块 " + moduleId + " 输出读取错误: " + e.getMessage());
            }
        });
        outputReader.setDaemon(true);
        outputReader.start();

        // 错误输出读取器
        errorReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 记录到日志服务
                    if (loggingService != null) {
                        loggingService.logModuleOutput(moduleId, "[ERROR] " + line);
                        // 只在当前模块被选中时才输出到控制台
                        if (loggingService.shouldPrintToConsole(moduleId)) {
                            System.err.println("[" + moduleId + "-ERROR] " + line);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("模块 " + moduleId + " 错误输出读取错误: " + e.getMessage());
            }
        });
        errorReader.setDaemon(true);
        errorReader.start();
    }

    public Process getProcess() {
        return process;
    }

    public OutputStream getStdin() {
        return stdin;
    }

    public String getModuleId() {
        return moduleId;
    }

    /**
     * 向进程发送命令
     */
    public void sendCommand(String command) {
        executor.submit(() -> {
            try {
                stdin.write((command + "\n").getBytes());
                stdin.flush();
            } catch (IOException e) {
                System.err.println("向模块 " + moduleId + " 发送命令失败: " + e.getMessage());
            }
        });
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        // 关闭stdin
        if (stdin != null) {
            try {
                stdin.close();
            } catch (IOException e) {
                System.err.println("关闭stdin失败: " + e.getMessage());
            }
        }
        
        // 中断并等待输出读取线程
        if (outputReader != null && outputReader.isAlive()) {
            outputReader.interrupt();
            try {
                outputReader.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 中断并等待错误读取线程
        if (errorReader != null && errorReader.isAlive()) {
            errorReader.interrupt();
            try {
                errorReader.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 关闭线程池
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}