package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lang {
    private static final String LANG_DIRECTORY = "lang";
    private static final String DEFAULT_LANGUAGE = "cn";
    private static final String LEGACY_LANG_FILE = "Lang.yml";
    private YamlConfiguration langConfig;
    private YamlConfiguration fallbackConfig;
    private String activeLanguage = DEFAULT_LANGUAGE;
    private final GoodsTrade plugin;

    public Lang(GoodsTrade plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        ensureLanguageFiles();
        activeLanguage = resolveLanguage();

        File langFile = getLanguageFile(activeLanguage);
        File fallbackFile = getLanguageFile(DEFAULT_LANGUAGE);
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        fallbackConfig = YamlConfiguration.loadConfiguration(fallbackFile);
        plugin.getLogger().info("Loaded language: " + activeLanguage);
    }

    public String getString(String path) {
        String value = langConfig.getString(path);
        if (value == null && !DEFAULT_LANGUAGE.equals(activeLanguage)) {
            value = fallbackConfig.getString(path);
        }
        if (value == null) {
            warnMissing(path);
            return "";
        }
        return color(value);
    }

    public String getString(String path, String defaultValue) {
        String value = langConfig.getString(path, defaultValue);
        return color(value);
    }

    public List<String> getStringList(String path) {
        List<String> values = langConfig.getStringList(path);
        if (values.isEmpty()) {
            String single = langConfig.getString(path);
            if (single != null) {
                List<String> result = new ArrayList<>();
                result.add(color(single));
                return result;
            }

            if (!DEFAULT_LANGUAGE.equals(activeLanguage)) {
                values = fallbackConfig.getStringList(path);
                if (values.isEmpty()) {
                    single = fallbackConfig.getString(path);
                    if (single != null) {
                        List<String> result = new ArrayList<>();
                        result.add(color(single));
                        return result;
                    }
                }
            }

            if (values.isEmpty()) {
                warnMissing(path);
                return new ArrayList<>();
            }
        }
        List<String> colored = new ArrayList<>();
        for (String value : values) {
            colored.add(color(value));
        }
        return colored;
    }

    public String replacePlaceholders(String text, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            plugin.getLogger().warning("占位符参数数量必须是偶数");
            return text;
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            text = text.replace(placeholders[i], placeholders[i + 1]);
        }
        return text;
    }

    private String resolveLanguage() {
        String configured = plugin.getConfig().getString("Language", DEFAULT_LANGUAGE);
        String language = configured == null ? DEFAULT_LANGUAGE : configured.trim().toLowerCase(Locale.ROOT);
        if (!"cn".equals(language) && !"en".equals(language)) {
            plugin.getLogger().warning("Unsupported language '" + configured + "'. Falling back to cn.");
            return DEFAULT_LANGUAGE;
        }
        return language;
    }

    private void ensureLanguageFiles() {
        File languageDirectory = new File(plugin.getDataFolder(), LANG_DIRECTORY);
        if (!languageDirectory.exists() && !languageDirectory.mkdirs()) {
            plugin.getLogger().warning("Could not create language directory: " + languageDirectory.getPath());
        }

        File chineseFile = getLanguageFile("cn");
        File legacyFile = new File(plugin.getDataFolder(), LEGACY_LANG_FILE);
        if (!chineseFile.exists() && legacyFile.exists()) {
            try {
                Files.copy(legacyFile.toPath(), chineseFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                plugin.getLogger().info("Migrated legacy Lang.yml to lang/cn.yml");
            } catch (IOException exception) {
                plugin.getLogger().warning("Could not migrate Lang.yml: " + exception.getMessage());
            }
        }

        saveLanguageResource("cn");
        saveLanguageResource("en");
    }

    private void saveLanguageResource(String language) {
        File file = getLanguageFile(language);
        if (!file.exists()) {
            plugin.saveResource(LANG_DIRECTORY + "/" + language + ".yml", false);
        }
    }

    private File getLanguageFile(String language) {
        return new File(new File(plugin.getDataFolder(), LANG_DIRECTORY), language + ".yml");
    }

    private void warnMissing(String path) {
        plugin.getLogger().warning("Missing language entry in lang/" + activeLanguage + ".yml: " + path);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
