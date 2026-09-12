package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class Lang {
    private static final String LANG_DIRECTORY = "lang";
    private static final String DEFAULT_LANGUAGE = "zh_cn";
    private static final String SYSTEM_LANGUAGE = "system";
    private static final String LEGACY_LANG_FILE = "Lang.yml";

    private final GoodsTrade plugin;
    private YamlConfiguration langConfig;
    private YamlConfiguration fallbackConfig;
    private YamlConfiguration bundledLangConfig;
    private YamlConfiguration bundledFallbackConfig;
    private String activeLanguage = DEFAULT_LANGUAGE;

    public Lang(GoodsTrade plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        ensureLanguageFiles();
        Set<String> availableLanguages = findInstalledLanguages();
        activeLanguage = resolveLanguage(availableLanguages);

        File langFile = getLanguageFile(activeLanguage);
        File fallbackFile = getLanguageFile(DEFAULT_LANGUAGE);
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        fallbackConfig = YamlConfiguration.loadConfiguration(fallbackFile);
        bundledLangConfig = loadBundledLanguage(activeLanguage);
        bundledFallbackConfig = loadBundledLanguage(DEFAULT_LANGUAGE);
        plugin.getLogger().info("Loaded language: " + activeLanguage);
        if (isSystemLanguage(plugin.getConfig().getString("Language"))) {
            for (String hint : systemLanguageHints(activeLanguage)) plugin.getLogger().info(hint);
        }
    }

    public String getString(String path) {
        String value = getStringOrFallback(path);
        if (value == null) {
            warnMissing(path);
            return "";
        }
        return color(value);
    }

    public String getString(String path, String defaultValue) {
        String value = getStringOrFallback(path);
        return color(value == null ? defaultValue : value);
    }

    public List<String> getStringList(String path) {
        List<String> values = getListOrFallback(path);
        if (values.isEmpty()) {
            String single = getStringOrFallback(path);
            if (single != null) {
                return Collections.singletonList(color(single));
            }
            warnMissing(path);
            return new ArrayList<>();
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

    public String getActiveLanguage() {
        return activeLanguage;
    }

    private String getStringOrFallback(String path) {
        String value = langConfig.getString(path);
        if (value == null) {
            value = bundledLangConfig.getString(path);
        }
        if (value == null && !DEFAULT_LANGUAGE.equals(activeLanguage)) {
            value = fallbackConfig.getString(path);
        }
        if (value == null) {
            value = bundledFallbackConfig.getString(path);
        }
        return value;
    }

    private List<String> getListOrFallback(String path) {
        List<String> values = langConfig.getStringList(path);
        if (values.isEmpty()) {
            values = bundledLangConfig.getStringList(path);
        }
        if (values.isEmpty() && !DEFAULT_LANGUAGE.equals(activeLanguage)) {
            values = fallbackConfig.getStringList(path);
        }
        if (values.isEmpty()) {
            values = bundledFallbackConfig.getStringList(path);
        }
        return values;
    }

    private YamlConfiguration loadBundledLanguage(String language) {
        String resourcePath = LANG_DIRECTORY + "/" + language + ".yml";
        InputStream stream = plugin.getResource(resourcePath);
        if (stream == null) return new YamlConfiguration();
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not load bundled language " + resourcePath + ": " + exception.getMessage());
            return new YamlConfiguration();
        }
    }

    private String resolveLanguage(Set<String> availableLanguages) {
        String configured = plugin.getConfig().getString("Language");
        if (isSystemLanguage(configured)) {
            String detected = detectSystemLanguage(Locale.getDefault(), System.getenv());
            String matched = matchSystemLanguage(detected, availableLanguages);
            if (matched != null) return matched;
            warnUnsupported("system language " + detected, availableLanguages);
            return DEFAULT_LANGUAGE;
        }
        String requested = normalizeLanguageCode(configured);
        if (availableLanguages.contains(requested)) {
            return requested;
        }

        warnUnsupported("configured language " + configured, availableLanguages);
        return DEFAULT_LANGUAGE;
    }

    private void ensureLanguageFiles() {
        File languageDirectory = new File(plugin.getDataFolder(), LANG_DIRECTORY);
        if (!languageDirectory.exists() && !languageDirectory.mkdirs()) {
            plugin.getLogger().warning("Could not create language directory: " + languageDirectory.getPath());
        }

        migrateLegacyLanguage(new File(plugin.getDataFolder(), LEGACY_LANG_FILE), DEFAULT_LANGUAGE);

        Set<String> resources = findBundledLanguageResources();
        for (String resource : resources) {
            File output = new File(plugin.getDataFolder(), resource.replace('/', File.separatorChar));
            if (output.exists()) continue;
            try {
                plugin.saveResource(resource, false);
                plugin.getLogger().info("Generated language file: " + resource);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Could not extract " + resource + ": " + exception.getMessage());
            }
        }

        if (!getLanguageFile(DEFAULT_LANGUAGE).exists()) {
            plugin.getLogger().severe("Default language file lang/" + DEFAULT_LANGUAGE + ".yml is missing.");
        }
    }

    private void migrateLegacyLanguage(File source, String targetLanguage) {
        File target = getLanguageFile(targetLanguage);
        if (!source.exists() || target.exists()) return;
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            plugin.getLogger().info("Migrated " + source.getName() + " to lang/" + targetLanguage + ".yml");
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not migrate " + source.getPath() + ": " + exception.getMessage());
        }
    }

    private Set<String> findBundledLanguageResources() {
        Set<String> resources = new TreeSet<>();
        try {
            URI location = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            Path codePath = Paths.get(location);
            if (Files.isDirectory(codePath)) {
                collectDirectoryResources(codePath.resolve(LANG_DIRECTORY), resources);
            } else {
                try (JarFile jarFile = new JarFile(codePath.toFile())) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (!entry.isDirectory() && name.startsWith(LANG_DIRECTORY + "/") && isYamlFile(name)) {
                            resources.add(name);
                        }
                    }
                }
            }

            if (resources.isEmpty()) {
                URL languageUrl = plugin.getClass().getClassLoader().getResource(LANG_DIRECTORY);
                if (languageUrl != null && "file".equalsIgnoreCase(languageUrl.getProtocol())) {
                    collectDirectoryResources(Paths.get(languageUrl.toURI()), resources);
                }
            }
        } catch (Exception exception) {
            plugin.getLogger().warning("Could not scan bundled language files: " + exception.getMessage());
        }

        return resources;
    }

    private void collectDirectoryResources(Path languagePath, Set<String> resources) throws IOException {
        if (!Files.isDirectory(languagePath)) return;
        try (Stream<Path> paths = Files.walk(languagePath)) {
            paths.filter(Files::isRegularFile)
                    .map(languagePath::relativize)
                    .map(Path::toString)
                    .filter(Lang::isYamlFile)
                    .map(path -> LANG_DIRECTORY + "/" + path.replace(File.separatorChar, '/'))
                    .forEach(resources::add);
        }
    }

    private Set<String> findInstalledLanguages() {
        Set<String> languages = new TreeSet<>();
        File directory = new File(plugin.getDataFolder(), LANG_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> isYamlFile(name));
        if (files == null) return languages;
        for (File file : files) {
            String name = file.getName();
            languages.add(name.substring(0, name.length() - 4).toLowerCase(Locale.ROOT));
        }
        return languages;
    }

    private File getLanguageFile(String language) {
        return new File(new File(plugin.getDataFolder(), LANG_DIRECTORY), language + ".yml");
    }

    private void warnUnsupported(String source, Set<String> availableLanguages) {
        plugin.getLogger().warning("Unsupported " + source + ". Available languages: "
                + availableLanguages + ". Falling back to " + DEFAULT_LANGUAGE + ".");
    }

    private void warnMissing(String path) {
        plugin.getLogger().warning("Missing language entry in lang/" + activeLanguage + ".yml: " + path);
    }

    static boolean isSystemLanguage(String configured) {
        return configured == null || configured.trim().isEmpty()
                || SYSTEM_LANGUAGE.equalsIgnoreCase(configured.trim());
    }

    static List<String> systemLanguageHints(String language) {
        List<String> hints = new ArrayList<>();
        hints.add("[语言设置] 当前为自动模式，已选择 " + language
                + "。如果当前语言不是您想要的语言，请修改 plugins/GoodsTrade/config.yml 中的 Language（如 zh_cn 或 en_us），然后执行 /gt reload。");
        hints.add("[Language] Automatic mode selected " + language
                + ". If this is not your preferred language, set Language to zh_cn or en_us in plugins/GoodsTrade/config.yml, then run /gt reload.");
        return hints;
    }

    /** 按进程消息语言的优先级读取本地环境，未指定具体语言时使用 JVM 默认语言。 */
    static String detectSystemLanguage(Locale jvmLocale, Map<String, String> environment) {
        for (String key : new String[]{"LC_ALL", "LC_MESSAGES", "LANG"}) {
            String value = environment.get(key);
            if (value == null || value.trim().isEmpty()) continue;
            String code = normalizeLanguageCode(value).split("[.@]", 2)[0];
            // C、POSIX 只表示通用运行环境，不能据此判断服主的语言偏好。
            // 高优先级变量存在时，不再读取被其覆盖的低优先级变量。
            if ("c".equals(code) || "posix".equals(code)) break;
            if (code.matches("[a-z]{2,8}(_[a-z0-9]{2,8})*")) return code;
            break;
        }
        return toLocaleCode(jvmLocale);
    }

    /** 优先精确匹配；英语、中文使用内置区域版本，其它语言仅在区域版本唯一时匹配。 */
    static String matchSystemLanguage(String code, Set<String> available) {
        if (available.contains(code)) return code;
        String base = code.split("_", 2)[0];
        if (available.contains(base)) return base;
        String preferred = "zh".equals(base) ? "zh_cn" : "en".equals(base) ? "en_us" : null;
        if (preferred != null && available.contains(preferred)) return preferred;
        String match = null;
        for (String candidate : available) {
            if (!candidate.startsWith(base + "_")) continue;
            if (match != null) return null;
            match = candidate;
        }
        return match;
    }
    private static String toLocaleCode(Locale locale) {
        String language = locale.getLanguage().toLowerCase(Locale.ROOT);
        String country = locale.getCountry().toLowerCase(Locale.ROOT);
        if (country.isEmpty()) return language;
        return language + "_" + country;
    }

    private static String normalizeLanguageCode(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replace('-', '_');
    }

    private static boolean isYamlFile(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        return lower.endsWith(".yml");
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
