package cc.sbsj.polang.goodstrade.commands.annotation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    // 默认实现getName方法，通过注解获取名称
    default String getName() {
        SubCommandAnnotation annotation = this.getClass().getAnnotation(SubCommandAnnotation.class);
        if (annotation != null) {
            return annotation.name();
        }
        throw new IllegalStateException("子命令实现必须重写getName()方法或使用@SubCommandAnnotation注解");
    }

    boolean execute(CommandSender sender, Command cmd, String label, String[] args);

    List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args);
}
