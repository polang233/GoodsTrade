//package cc.sbsj.polang.goodstrade.util;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//
//public class VersionUtils {
//    private static final int VERSION;
//
//    static {
//        VERSION = detectVersion();
//    }
//
//    private static int detectVersion() {
//        try {
//            String packageName = Bukkit.class.getPackage().getName();
//            String versionString = packageName.substring(packageName.lastIndexOf('.') + 1);
//
//            if (versionString.startsWith("v1_")) {
//                String[] parts = versionString.substring(3).split("_");
//                int major = Integer.parseInt(parts[0]);
//                int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
//
//                if (major >= 1 && minor >= 13) {
//                    return 13;
//                } else {
//                    return 12;
//                }
//            }
//        } catch (Exception e) {
//            try {
//                String bukkitVersion = Bukkit.getBukkitVersion();
//                String[] parts = bukkitVersion.split("\\.");
//                if (parts.length >= 2) {
//                    int major = Integer.parseInt(parts[0]);
//                    int minor = Integer.parseInt(parts[1]);
//
//                    if (major >= 1 && minor >= 13) {
//                        return 13;
//                    } else {
//                        return 12;
//                    }
//                }
//            } catch (Exception ex) {
//            }
//        }
//
//        return 12;
//    }
//
//    public static boolean isNew() {
//        return VERSION >= 13;
//    }
//
//    public static boolean isOld() {
//        return VERSION < 13;
//    }
//
//    public static int getVersion() {
//        return VERSION;
//    }
//
//    public static Material getGlassPane(int colorCode) {
//        if (isNew()) {
//            String[] glassMaterials = {
//                    "WHITE_STAINED_GLASS_PANE",
//                    "ORANGE_STAINED_GLASS_PANE",
//                    "MAGENTA_STAINED_GLASS_PANE",
//                    "LIGHT_BLUE_STAINED_GLASS_PANE",
//                    "YELLOW_STAINED_GLASS_PANE",
//                    "LIME_STAINED_GLASS_PANE",
//                    "PINK_STAINED_GLASS_PANE",
//                    "GRAY_STAINED_GLASS_PANE",
//                    "LIGHT_GRAY_STAINED_GLASS_PANE",
//                    "CYAN_STAINED_GLASS_PANE",
//                    "PURPLE_STAINED_GLASS_PANE",
//                    "BLUE_STAINED_GLASS_PANE",
//                    "BROWN_STAINED_GLASS_PANE",
//                    "GREEN_STAINED_GLASS_PANE",
//                    "RED_STAINED_GLASS_PANE",
//                    "BLACK_STAINED_GLASS_PANE"
//            };
//
//            if (colorCode >= 0 && colorCode < glassMaterials.length) {
//                try {
//                    return Material.valueOf(glassMaterials[colorCode]);
//                } catch (IllegalArgumentException e) {
//                    return getFallbackGlassPane(colorCode);
//                }
//            }
//            return getFallbackGlassPane(15);
//        } else {
//            return Material.STAINED_GLASS_PANE;
//        }
//    }
//
//    private static Material getFallbackGlassPane(int colorCode) {
//        try {
//            return Material.valueOf("STAINED_GLASS_PANE");
//        } catch (Exception e) {
//            return Material.AIR;
//        }
//    }
//
//    public static Material getIronFence() {
//        if (isNew()) {
//            try {
//                return Material.valueOf("IRON_BARS");
//            } catch (IllegalArgumentException e) {
//                return getFallbackIronFence();
//            }
//        } else {
//            return Material.IRON_FENCE;
//        }
//    }
//
//    private static Material getFallbackIronFence() {
//        try {
//            return Material.valueOf("IRON_FENCE");
//        } catch (Exception e) {
//            return Material.AIR;
//        }
//    }
//}
