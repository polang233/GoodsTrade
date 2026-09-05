package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.view.View;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ViewConfig {
    private static final String VIEW_ITEMS_FILE = "View.yml";

    public static void load(GoodsTrade plugin) {
        View.resetDefaultItems();

        File viewItemsFile = new File(plugin.getDataFolder(), VIEW_ITEMS_FILE);
        if (!viewItemsFile.exists()) {
            plugin.saveResource(VIEW_ITEMS_FILE, false);
        }

        YamlConfiguration viewItemsConfig = YamlConfiguration.loadConfiguration(viewItemsFile);
        if (!viewItemsConfig.isConfigurationSection("button")) {
            plugin.getLogger().warning(VIEW_ITEMS_FILE + " 缺少 button 配置节点，界面物品将使用默认值。");
            return;
        }

        View.backGround = loadButtonItem(viewItemsConfig, "#", View.backGround);
        View.infoItem = loadButtonItem(viewItemsConfig, "@", View.infoItem);

        View.senderReadyButtonItem = loadButtonItem(viewItemsConfig, "F", View.senderReadyButtonItem);
        View.senderReadyButtonItemYes = loadButtonItem(viewItemsConfig, "F:Yes", View.senderReadyButtonItemYes);
        View.senderReadyButtonItemWait = loadButtonItem(viewItemsConfig, "Wait", View.senderReadyButtonItemWait);

        View.targetReadyButtonItem = loadButtonItem(viewItemsConfig, "T", View.targetReadyButtonItem);
        View.targetReadyButtonItemYes = loadButtonItem(viewItemsConfig, "T:Yes", View.targetReadyButtonItemYes);
        View.targetReadyButtonItemWait = loadButtonItem(viewItemsConfig, "Wait", View.targetReadyButtonItemWait);

        View.cancelReadyItem = loadButtonItem(viewItemsConfig, "CancelReady", View.cancelReadyItem);

        ItemStack baseMoneyButton = loadButtonItem(viewItemsConfig, "Money", View.moneyButtonItems.get(0));
        for (int index = 0; index < View.moneyButtonItems.size(); index++) {
            View.moneyButtonItems.set(index, loadButtonItem(
                    viewItemsConfig,
                    "Money-" + (index + 1),
                    baseMoneyButton.clone()
            ));
        }
    }

    static ItemStack loadButtonItem(YamlConfiguration config, String key, ItemStack defaultItem) {
        String path = "button." + key;
        if (!config.isConfigurationSection(path)) {
            if (config.get(path) != null) {
                warnConfig(path + " 必须是配置节点，已使用默认值。");
            }
            return defaultItem;
        }

        ConfigurationSection section = config.getConfigurationSection(path);
        ItemStack item = defaultItem.clone();
        String material = section.getString("material");
        if (isConfigured(material)) {
            ItemStack configuredItem = parseMaterialItem(material);
            if (configuredItem != null) {
                if (configuredItem.getItemMeta() == null) {
                    warnConfig("button." + key + ".material 无法显示按钮说明，已保留默认物品。");
                } else {
                    // 换材质时保留基础样式，再由下方的显式配置逐项覆盖。
                    // Money-1 等分档按钮也由此继承 Money 的名称、Lore 和模型数据。
                    configuredItem.setItemMeta(item.getItemMeta());
                    configuredItem.setAmount(item.getAmount());
                    item = configuredItem;
                }
            } else {
                warnConfig("button." + key + ".material 的材质无效: " + material);
            }
        }

        int customModelData = getCustomModelData(section, key);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            if (hasMetaConfig(section, customModelData)) {
                warnConfig("button." + key + " 使用的材质没有可编辑的物品数据，name/lore/custom_model_data 已跳过。");
            }
            return item;
        }

        String name = section.getString("name");
        if (isConfigured(name)) {
            meta.setDisplayName(color(name));
        }

        if (section.contains("lore") || section.contains("description")) {
            List<String> lore = section.contains("lore") ? getStringList(section, "lore") : getStringList(section, "description");
            if (!isDefaultList(lore)) {
                meta.setLore(color(lore));
            }
        }

        if (customModelData > 0) {
            setCustomModelData(meta, customModelData, key);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack parseMaterialItem(String material) {
        String materialName = material.trim();
        if (materialName.toLowerCase(Locale.ROOT).startsWith("minecraft:")) {
            materialName = materialName.substring("minecraft:".length());
        }

        Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(materialName.toUpperCase(Locale.ROOT));
        if (xMaterial.isPresent()) {
            ItemStack item = xMaterial.get().parseItem();
            return item == null ? null : item;
        }

        Material bukkitMaterial = Material.matchMaterial(materialName.toUpperCase(Locale.ROOT));
        if (bukkitMaterial != null) {
            return new ItemStack(bukkitMaterial);
        }
        return null;
    }

    private static List<String> getStringList(ConfigurationSection section, String path) {
        if (section.isList(path)) {
            return section.getStringList(path);
        }
        String value = section.getString(path);
        if (value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(value);
    }

    private static int getCustomModelData(ConfigurationSection section, String key) {
        if (section.contains("custom_model_data")) {
            return parsePositiveInt(section.get("custom_model_data"), "button." + key + ".custom_model_data");
        }
        if (section.contains("custom-model-data")) {
            return parsePositiveInt(section.get("custom-model-data"), "button." + key + ".custom-model-data");
        }
        if (section.contains("model")) {
            return parsePositiveInt(section.get("model"), "button." + key + ".model");
        }
        return 0;
    }

    private static int parsePositiveInt(Object value, String path) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            int number = ((Number) value).intValue();
            if (number < 0) {
                warnConfig(path + " 不能小于 0，已按默认值处理。");
                return 0;
            }
            return number;
        }
        String text = String.valueOf(value).trim();
        if (!isConfigured(text)) {
            return 0;
        }
        try {
            int number = Integer.parseInt(text);
            if (number < 0) {
                warnConfig(path + " 不能小于 0，已按默认值处理。");
                return 0;
            }
            return number;
        } catch (NumberFormatException ignored) {
            warnConfig(path + " 必须是数字，当前值无效: " + text);
            return 0;
        }
    }

    private static void setCustomModelData(ItemMeta meta, int customModelData, String key) {
        try {
            Method method = meta.getClass().getMethod("setCustomModelData", Integer.class);
            method.invoke(meta, customModelData);
        } catch (NoSuchMethodException ignored) {
            warnConfig("当前服务端版本不支持 custom_model_data，已跳过 button." + key);
        } catch (Exception e) {
            warnConfig("设置 button." + key + " 的 custom_model_data 失败: " + e.getMessage());
        }
    }

    private static boolean hasMetaConfig(ConfigurationSection section, int customModelData) {
        return isConfigured(section.getString("name"))
                || hasConfiguredList(section, "lore")
                || hasConfiguredList(section, "description")
                || customModelData > 0;
    }

    private static boolean hasConfiguredList(ConfigurationSection section, String path) {
        if (!section.contains(path)) {
            return false;
        }
        List<String> values = getStringList(section, path);
        return !values.isEmpty() && !isDefaultList(values);
    }

    private static void warnConfig(String message) {
        GoodsTrade.instance.getLogger().warning(VIEW_ITEMS_FILE + " 中 " + message);
    }

    private static boolean isConfigured(String value) {
        return value != null && !value.trim().isEmpty() && !"@default@".equalsIgnoreCase(value.trim());
    }

    private static boolean isDefaultList(List<String> values) {
        return values.size() == 1 && !isConfigured(values.get(0));
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static List<String> color(List<String> texts) {
        List<String> colored = new ArrayList<>();
        for (String text : texts) {
            colored.add(color(text == null ? "" : text));
        }
        return colored;
    }
}
