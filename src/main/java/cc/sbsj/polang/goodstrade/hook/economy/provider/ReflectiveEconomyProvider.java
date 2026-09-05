package cc.sbsj.polang.goodstrade.hook.economy.provider;

import cc.sbsj.polang.goodstrade.hook.economy.EconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;

import org.bukkit.plugin.Plugin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/** 启动时解析 API 方法，旧版 Java/服务端不必加载新经济插件的类。 */
abstract class ReflectiveEconomyProvider implements EconomyProvider {
    protected final Plugin plugin;
    protected final Object api;

    ReflectiveEconomyProvider(Plugin plugin) throws ReflectiveOperationException {
        this.plugin = plugin;
        this.api = plugin.getClass().getMethod("getAPI").invoke(plugin);
        if (api == null) throw new IllegalStateException("API 尚未就绪");
    }

    protected Object invoke(Method method, Object receiver, Object... arguments) {
        if (!plugin.isEnabled()) throw new IllegalStateException("经济插件已停用");
        try {
            return method.invoke(receiver, arguments);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("经济 API 调用失败", exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("经济 API 不兼容", exception);
        }
    }

    @Override public boolean isAvailable() { return plugin.isEnabled(); }
    @Override public String getName() { return plugin.getName(); }
    @Override public String format(BigDecimal amount) { return amount.stripTrailingZeros().toPlainString(); }

    protected EconomyTransactionResult result(Object response) {
        return Boolean.TRUE.equals(response) ? EconomyTransactionResult.success()
                : EconomyTransactionResult.failure("经济插件拒绝操作");
    }
}
