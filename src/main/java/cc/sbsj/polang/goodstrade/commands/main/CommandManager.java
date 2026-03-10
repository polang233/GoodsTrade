package cc.sbsj.polang.goodstrade.commands.main;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;

public class CommandManager {
    private final Map<String, SubCommand> commands = new HashMap<>();

    public void registerCommand(SubCommand command) {
        commands.put(command.getName(), command);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(GoodsTrade.PREFIX + "§7可输入以下命令:");
            for (SubCommand command : commands.values()) {
                sender.sendMessage(GoodsTrade.PREFIX + "/GoodsTrade " + command.getName());
            }
            return false;
        }
        String subCommandName = args[0];
        SubCommand subCommand = commands.get(subCommandName);
        if (subCommand != null) {
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            return subCommand.execute(sender, cmd, label, subArgs);
        } else {
            sender.sendMessage(GoodsTrade.PREFIX + "§c未知的子命令.");
            return false;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(commands.keySet());
        }
        String subCommandName = args[0];
        SubCommand subCommand = commands.get(subCommandName);
        if (subCommand != null) {
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            return subCommand.tabComplete(sender, cmd, label, subArgs);
        }
        return Collections.emptyList();
    }

    public Map<String, SubCommand> getCommands() {
        return commands;
    }

    /**
     * 注册给定包中所有带 @SubCommandAnnotation 注解且实现 SubCommand 的类
     */
    public void registerAnnotatedCommands(String packageName, JavaPlugin plugin) {
        try {
            Set<Class<?>> classes = getClasses(packageName, plugin);
            GoodsTrade.instance.getLogger().info(GoodsTrade.PREFIX + "扫描到 " + classes.size() + " 个命令类");

            for (Class<?> clazz : classes) {
                if (SubCommand.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(SubCommandAnnotation.class)) {
                    try {
                        SubCommand instance = (SubCommand) clazz.getDeclaredConstructor().newInstance();
                        registerCommand(instance);
                        GoodsTrade.instance.getLogger().info(GoodsTrade.PREFIX + "已注册子命令: " + clazz.getName());
                    } catch (Exception e) {
                        GoodsTrade.instance.getLogger().warning(GoodsTrade.PREFIX + "无法实例化命令类: " + clazz.getName() + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            GoodsTrade.instance.getLogger().severe(GoodsTrade.PREFIX + "扫描命令类时出错: " + e.getMessage());
        }
    }

    private Set<Class<?>> getClasses(String packageName, JavaPlugin plugin) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader loader = plugin.getClass().getClassLoader();
        String path = packageName.replace('.', '/');

        URL resource = loader.getResource(path);
        if (resource != null) {
            String protocol = resource.getProtocol();
            GoodsTrade.instance.getLogger().info(GoodsTrade.PREFIX + "扫描协议: " + protocol + ", 路径: " + resource);

            try {
                if ("jar".equals(protocol)) {
                    scanJar(resource, path, loader, classes);
                } else if ("file".equals(protocol)) {
                    scanDirectory(new File(resource.getFile()), packageName, loader, classes);
                }
            } catch (Exception e) {
                GoodsTrade.instance.getLogger().severe(GoodsTrade.PREFIX + "扫描过程中发生错误: " + e.getMessage());
            }
        }

        return classes;
    }

    private void scanJar(URL jarUrl, String basePath, ClassLoader loader, Set<Class<?>> classes) {
        try {
            String jarPath = jarUrl.getPath();
            int separator = jarPath.indexOf("!/");
            if (separator == -1) return;

            String realJarPath = URLDecoder.decode(jarPath.substring(5, separator), StandardCharsets.UTF_8);
            try (JarFile jar = new JarFile(realJarPath)) {
                jar.stream()
                        .filter(entry -> !entry.isDirectory())
                        .filter(entry -> entry.getName().startsWith(basePath + "/"))
                        .filter(entry -> entry.getName().endsWith(".class"))
                        .filter(entry -> !entry.getName().contains("$"))
                        .filter(entry -> {
                            // 计算相对路径
                            String relativePath = entry.getName().substring(basePath.length() + 1);
                            // 只允许两种情况：
                            // 1. 没有'/'，即直接在basePath目录下的类
                            // 2. 只有一个'/'，即在basePath的直接子目录下的类
                            int firstSlash = relativePath.indexOf('/');
                            int lastSlash = relativePath.lastIndexOf('/');
                            return firstSlash == -1 || (firstSlash == lastSlash && firstSlash < relativePath.length() - 1);
                        })
                        .forEach(entry -> {
                            String className = entry.getName()
                                    .substring(0, entry.getName().length() - 6)
                                    .replace('/', '.');
                            try {
                                classes.add(Class.forName(className, false, loader));
                            } catch (ClassNotFoundException e) {
                                GoodsTrade.instance.getLogger().warning(GoodsTrade.PREFIX + "类未找到: " + className);
                            }
                        });
            }
        } catch (IOException e) {
            GoodsTrade.instance.getLogger().severe(GoodsTrade.PREFIX + "JAR文件扫描错误: " + e.getMessage());
        }
    }

    private void scanDirectory(File dir, String packageName, ClassLoader loader, Set<Class<?>> classes) {
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 扫描一层子目录中的类文件，但不递归扫描更深层的目录
                File[] subFiles = file.listFiles();
                if (subFiles == null) continue;

                for (File subFile : subFiles) {
                    // 只处理直接在子目录中的类文件，忽略更深层的目录
                    if (subFile.isFile() && subFile.getName().endsWith(".class")) {
                        String simpleName = subFile.getName().substring(0, subFile.getName().length() - 6);
                        String className = packageName + '.' + file.getName() + '.' + simpleName;
                        try {
                            classes.add(Class.forName(className, false, loader));
                        } catch (ClassNotFoundException e) {
                            GoodsTrade.instance.getLogger().warning(GoodsTrade.PREFIX + file.getName() + "扫描到 " + simpleName + " 子命令");
                        }
                    }
                }
            } else if (file.getName().endsWith(".class")) {
                String simpleName = file.getName().substring(0, file.getName().length() - 6);
                String className = packageName + '.' + simpleName;
                try {
                    classes.add(Class.forName(className, false, loader));
                } catch (ClassNotFoundException e) {
                    GoodsTrade.instance.getLogger().warning(GoodsTrade.PREFIX + "类未找到: " + className);
                }
            }
        }
    }
}