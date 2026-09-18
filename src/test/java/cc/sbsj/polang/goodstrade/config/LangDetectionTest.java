package cc.sbsj.polang.goodstrade.config;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class LangDetectionTest {
    private final Set<String> available = new HashSet<>(Arrays.asList("zh_cn", "en_us"));
    @Test public void chineseLinuxEnvironmentOverridesEnglishJvm() {
        assertEquals("zh_cn", Lang.detectSystemLanguage(Locale.US, Collections.singletonMap("LANG", "zh_CN.UTF-8")));
    }
    @Test public void messageLocalePrecedenceIsRespected() {
        Map<String, String> env = new HashMap<>();
        env.put("LANG", "en_US.UTF-8");
        env.put("LC_MESSAGES", "zh_CN.UTF-8");
        assertEquals("zh_cn", Lang.detectSystemLanguage(Locale.US, env));
        env.put("LC_ALL", "en_GB.UTF-8");
        assertEquals("en_gb", Lang.detectSystemLanguage(Locale.CHINA, env));
        env.put("LC_ALL", " ");
        assertEquals("zh_cn", Lang.detectSystemLanguage(Locale.US, env));
    }
    @Test public void neutralOrMissingEnvironmentFallsBackToJvm() {
        for (String neutral : Arrays.asList("C", "C.UTF-8", "POSIX", "invalid value")) {
            Map<String, String> env = new HashMap<>();
            env.put("LC_ALL", neutral);
            env.put("LANG", "en_US.UTF-8");
            assertEquals("zh_cn", Lang.detectSystemLanguage(Locale.CHINA, env));
        }
        assertEquals("en_us", Lang.detectSystemLanguage(Locale.US, Collections.emptyMap()));
    }
    @Test public void encodingsModifiersAndHyphensAreNormalized() {
        assertEquals("zh_cn", Lang.detectSystemLanguage(Locale.US, Collections.singletonMap("LANG", " zh-CN.UTF-8@variant ")));
    }
    @Test public void supportedRegionalVariantsAndBaseLanguagesMatch() {
        assertEquals("en_us", Lang.matchSystemLanguage("en_gb", available));
        assertEquals("zh_cn", Lang.matchSystemLanguage("zh", available));
        assertNull(Lang.matchSystemLanguage("ja_jp", available));
        available.add("zh_tw");
        assertEquals("zh_tw", Lang.matchSystemLanguage("zh_tw", available));
        available.add("de_de");
        assertEquals("de_de", Lang.matchSystemLanguage("de_at", available));
        available.add("de_ch");
        assertNull(Lang.matchSystemLanguage("de_at", available));
    }
    @Test public void automaticModeHintsIncludeBothLanguagesAndInstructions() {
        assertTrue(Lang.isSystemLanguage(null));
        assertTrue(Lang.isSystemLanguage(" System "));
        assertTrue(Lang.isSystemLanguage(" "));
        assertFalse(Lang.isSystemLanguage("zh_cn"));
        assertFalse(Lang.isSystemLanguage("en_us"));
        List<String> hints = Lang.systemLanguageHints("en_us");
        assertEquals(2, hints.size());
        assertTrue(hints.get(0).contains("语言设置"));
        assertTrue(hints.get(1).contains("If this is not your preferred language"));
        for (String hint : hints) {
            assertTrue(hint.contains("en_us"));
            assertTrue(hint.contains("plugins/GoodsTrade/config.yml"));
            assertTrue(hint.contains("/gt reload"));
        }
    }

    @Test public void missingLanguageKeysAreCopiedWithoutChangingExistingValues() {
        MemoryConfiguration current = new MemoryConfiguration();
        current.set("trade-request.received", "custom");
        current.set("trade-request.hover", "");
        MemoryConfiguration bundled = new MemoryConfiguration();
        bundled.set("trade-request.received", "default");
        bundled.set("trade-request.hover", "default-hover");
        bundled.set("trade-request.received-shift-hint", "hint");
        bundled.set("trade-view.click-confirm-lore", Arrays.asList("", "click"));
        List<String> added = Lang.fillMissingKeys(current, bundled);
        assertEquals(Arrays.asList("trade-request.received-shift-hint", "trade-view.click-confirm-lore"), added);
        assertEquals("custom", current.getString("trade-request.received"));
        assertEquals("", current.getString("trade-request.hover"));
        assertEquals("hint", current.getString("trade-request.received-shift-hint"));
        assertEquals(Arrays.asList("", "click"), current.getStringList("trade-view.click-confirm-lore"));
        assertTrue(Lang.fillMissingKeys(current, bundled).isEmpty());
    }

    @Test public void addedLanguageKeyLogTruncatesLongLists() {
        assertEquals("a, b", Lang.formatAddedKeys(Arrays.asList("a", "b")));
        List<String> keys = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
        assertEquals("1, 2, 3, 4, 5, 6, 7, 8 ...", Lang.formatAddedKeys(keys));
    }
}
