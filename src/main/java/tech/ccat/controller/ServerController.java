package tech.ccat.controller;

import tech.ccat.controller.command.*;
import tech.ccat.controller.config.ServerConfig;
import tech.ccat.controller.config.ModuleConfig;
import tech.ccat.controller.service.ModuleManagementService;
import tech.ccat.controller.service.LoggingService;
import tech.ccat.controller.service.AutoStartService;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * 服务器控制器主类
 */
public class ServerController {
    private final ModuleManagementService moduleService;
    private final LoggingService loggingService;
    private final CommandRegistry commandRegistry;
    private AutoStartService autoStartService;
    private final SwitchCommand switchCommand;
    private boolean running = true;
    private ServerConfig serverConfig;

    public ServerController() {
        this.loggingService = new LoggingService();
        this.moduleService = new ModuleManagementService(loggingService);
        this.commandRegistry = new CommandRegistry();
        this.switchCommand = new SwitchCommand(moduleService, loggingService);
        this.serverConfig = new ServerConfig();
    }

    /**
     * 初始化应用
     */
    public void initialize() {
        // 加载配置
        loadConfiguration();
        
        // 创建自动启动服务（使用加载后的配置）
        this.autoStartService = new AutoStartService(moduleService, serverConfig);
        
        // 注册模块
        registerModules();
        
        // 初始化命令系统
        initializeCommands();
        
        // 启动自动启动服务
        autoStartService.init();
    }

    private void loadConfiguration() {
        try {
            String configPath = System.getProperty("user.dir") + File.separator + "config.yml";
            File configFile = new File(configPath);
            
            if (!configFile.exists()) {
                System.out.println("配置文件不存在: " + configPath);
                System.out.println("使用默认配置");
                serverConfig = new ServerConfig(1000, 5000, null);
                return;
            }
            
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new Constructor(ServerConfig.class, options));
            serverConfig = yaml.load(new FileInputStream(configFile));
            System.out.println("配置文件加载成功: " + configPath);
            
        } catch (FileNotFoundException e) {
            System.err.println("配置文件加载失败: " + e.getMessage());
            serverConfig = new ServerConfig(1000, 5000, null);
        } catch (Exception e) {
            System.err.println("配置文件解析失败: " + e.getMessage());
            serverConfig = new ServerConfig(1000, 5000, null);
        }
    }

    private void registerModules() {
        if (serverConfig.getModules() == null || serverConfig.getModules().isEmpty()) {
            System.out.println("没有配置模块");
            return;
        }
        
        for (ModuleConfig moduleConfig : serverConfig.getModules()) {
            moduleService.registerModule(moduleConfig.getName(), moduleConfig);
        }
        
        System.out.println("模块注册完成，共注册 " + serverConfig.getModules().size() + " 个模块");
    }

    private void initializeCommands() {
        commandRegistry.registerCommand(new StartCommand(moduleService));
        commandRegistry.registerCommand(new StopCommand(moduleService));
        commandRegistry.registerCommand(new KillCommand(moduleService));
        commandRegistry.registerCommand(new LogsCommand(loggingService));
        commandRegistry.registerCommand(new StatusCommand(moduleService));
        commandRegistry.registerCommand(switchCommand);
        commandRegistry.registerCommand(new HelpCommand(commandRegistry, switchCommand));
        commandRegistry.registerCommand(new ExitCommand(this));
    }

    /**
     * 运行主程序
     */
    public void run() {
        System.out.println("=== ServerController 启动 ===");
        System.out.println("输入 'help' 查看可用命令");
        
        Scanner scanner = new Scanner(System.in);
        while (running) {
            try {
                // 根据当前模式显示不同的提示符
                if (switchCommand.isInModuleView()) {
                    System.out.print("模块[" + switchCommand.getCurrentModuleId() + "] > ");
                } else {
                    System.out.print("> ");
                }
                
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                String[] args = input.split("\\s+");
                Command command = commandRegistry.getCommand(args[0]);
                
                if (command != null) {
                    command.execute(args);
                } else {
                    System.out.println("未知命令: " + args[0]);
                    if (!switchCommand.isInModuleView()) {
                        System.out.println("输入 'help' 查看可用命令");
                    }
                }
            } catch (Exception e) {
                System.err.println("命令执行错误: " + e.getMessage());
                // 在模块视图中简化错误输出
                if (!switchCommand.isInModuleView()) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
    }

    /**
     * 关闭程序
     */
    public void shutdown() {
        running = false;
        moduleService.stopAllModules();
        autoStartService.shutdown();
    }

    /**
     * 主程序入口
     */
    public static void main(String[] args) {
        ServerController controller = new ServerController();
        controller.initialize();
        controller.run();
    }
}