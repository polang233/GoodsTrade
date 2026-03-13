package cc.sbsj.polang.goodstrade.util;

public class VersionUtils {
    private static final int VERSION;

    static {
        String version = getBukkitVersion();
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        if (major >= 1 && minor >= 16) {
            VERSION = 16;
        } else {
            VERSION = 12;
        }
    }

    private static String getBukkitVersion() {
        try {
            Class<?> clazz = Class.forName("org.bukkit.Bukkit");
            Object result = clazz.getMethod("getVersion").invoke(null);
            return (String) result;
        } catch (Exception e) {
            return "1.12.2";
        }
    }

    public static boolean isNew() {
        return VERSION >= 16;
    }

    public static boolean isOld() {
        return VERSION < 16;
    }

    public static int getVersion() {
        return VERSION;
    }
}
