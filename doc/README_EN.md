# GoodsTrade

GoodsTrade is a lightweight, inventory-based trading plugin for Minecraft servers. It gives both players a shared trade menu, locks offers once they are confirmed, and waits through a final countdown before exchanging the items.

> 中文文档请查看 [README.md](../README.md)。

<p align="center">
  <img src="../img/QQ20260315-190201-HD.gif" alt="GoodsTrade trade menu demonstration">
</p>

![Version](https://img.shields.io/github/v/release/polang233/GoodsTrade?label=Version&color=2ea44f)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.2-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)
[![Powered by OrcaRouter](https://img.shields.io/badge/Powered_by-OrcaRouter-2563eb)](https://www.orcarouter.ai/ref/ref_1095590f527d3ae7ae2f)

## Why use GoodsTrade?

- **Two-player confirmation:** the trade only completes after both players approve their offers.
- **Final countdown:** either side can stop the confirmation before the exchange is committed.
- **Locked offers:** confirmed players cannot quietly swap items at the last moment.
- **Multiple currencies:** Vault, PlayerPoints, ExcellentEconomy, and Minecraft experience level offers can be combined in one trade. Changes reset existing confirmations.
- **Safe returns:** cancelling or closing the menu returns offered items; overflow is dropped at the player's location instead of disappearing.
- **Quick requests:** players can use a command or sneak-right-click another player.
- **Trade preferences:** each player can disable incoming requests when they want some peace and quiet.
- **Item rules:** block items by display name, lore text, an NBT path, or an NBT path/value pair.
- **Custom menus:** change button materials, names, lore, and custom model data in `View.yml`.
- **Optional protection:** prevent damage or block movement while a trade is open.
- **PlaceholderAPI hook:** read whether a player currently accepts trade requests.
- **Chinese and English messages:** switch languages from `config.yml` and edit either language file freely.

## Requirements

- Minecraft `1.12` through `26.2`
- Bukkit, Spigot, or Paper
- Java 8 or newer
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is optional
- Money trading optionally uses a [Vault](https://www.spigotmc.org/resources/vault.34315/) economy service, PlayerPoints, or ExcellentEconomy

GoodsTrade does **not** currently claim Folia support.

## Installation

1. Download the latest Lite build from [GitHub Releases](https://github.com/polang233/GoodsTrade/releases/latest).
2. Place the JAR in your server's `plugins` directory.
3. Start the server once to generate the configuration files.
4. Edit the files under `plugins/GoodsTrade/` and run `/gt reload`.

## Trading workflow

1. Send a request with `/gt sendtrade <player>`, or sneak-right-click the player when that trigger is enabled.
2. The other player clicks the acceptance message or runs `/gt accept`.
3. Each player places items in their own side of the menu.
4. Both players confirm their offers.
5. The configured countdown begins. Either player can cancel during this step.
6. When the countdown ends, GoodsTrade exchanges the items and closes the menu.

## Commands

| Command | Permission | Purpose |
|---|---|---|
| `/gt sendtrade <player>` | `goodstrade.command.sendtrade` | Send a trade request |
| `/gt accept` | `goodstrade.command.accept` | Accept the only pending request |
| `/gt accept <player>` | `goodstrade.command.accept` | Accept a request from a specific player |
| `/gt toggle [true\|false]` | `goodstrade.command.toggle` | Enable, disable, or toggle incoming requests |
| `/gt trade <sender> <receiver>` | `goodstrade.command.trade` | Open a trade between two players as an administrator |
| `/gt test [test-name]` | `goodstrade.command.test` | Open test mode with a test player name |
| `/gt reload` | `goodstrade.command.reload` | Reload configuration, menu items, blacklist rules, and language messages |
| `/gt` | `goodstrade.command` | Show available subcommands |

The player-facing command permissions default to everyone. `trade` and `reload` default to server operators.

## Language selection

New installations extract every bundled translation from the JAR. Version 1.1.9 includes:

```text
plugins/GoodsTrade/lang/zh_cn.yml
plugins/GoodsTrade/lang/en_us.yml
```

### Automatic language detection

With `Language: system` (also when missing or blank), GoodsTrade reads the first nonempty process variable in this order: `LC_ALL`, `LC_MESSAGES`, `LANG`. Locale forms such as `zh_CN.UTF-8` are normalized. Neutral `C`/`POSIX`, invalid values or no variables fall back to the JVM default. Exact installed translations take priority; Chinese and English can use `zh_cn` and `en_us`, while other languages require a unique regional match. Unsupported languages fall back to Simplified Chinese. Explicit language settings still require an exact translation filename.

Automatic mode prints both Chinese and English configuration hints on startup and reload, including the selected language, configuration path and reload command. Explicit language settings suppress these hints. Detection is local and performs no network requests. The server's location does not determine the process locale; services and containers can inherit a different environment than an interactive terminal. Use `Language: zh_cn` for deterministic Chinese output. Process environment changes require restarting Java.

You can also choose a locale explicitly in `config.yml`:

```yaml
Language: system
# Language: zh_cn
# Language: en_us
```

Run `/gt reload` after changing the value. Locale names follow the common Minecraft/i18n format, such as `zh_cn`, `en_us`, and `ja_jp`.

The command list shown by `/gt` reads its layout and descriptions from `command.help-entry` and `command.description` in the active language file.

GoodsTrade scans the complete `lang/` folder inside each new JAR and extracts any translation that is missing on disk. Existing files are never overwritten, so local edits are preserved while newly bundled languages appear automatically.

When upgrading from an older published version, the root-level `Lang.yml` is migrated to `lang/zh_cn.yml` when needed. The original file is left untouched.

## Main configuration

```yaml
Language: en_us

Trade:
  Enabled-Worlds: ["*"]
  Distance:
    Same-World: true
    Start: 4
    Trading: 8
  Wait-Time: 5
  Economy:
    Enable: false
    Allow-Negative: false
    Amounts:
      - 1000
      - 10000
  Triggers:
    Shift-Right-Click: true
  Safe:
    Close-On-Damage: false
    Damage: false
    Move: false
```

- `Wait-Time` controls the final confirmation countdown in seconds and is limited to 0–64; 0 skips the countdown.
- `Economy.Amounts` defines one to four button steps. Left click adds the step and right click subtracts it.
- `Allow-Negative` lets an offer cross below zero; a negative offer means the other player must pay. The divider always shows each player's resulting payment obligation.
- Balance checks run on every amount change, on confirmation, and immediately before settlement. Changing money resets any existing confirmation so both players must review again.
- `Shift-Right-Click` enables the quick request gesture.
- `Safe.Damage` cancels damage against players who are currently trading.
- `Safe.Move` stops block-to-block movement while the trade menu is open.

`Enabled-Worlds` defaults to `["*"]`, allowing all worlds, including custom and newly created worlds. Existing explicit world lists remain effective; use `["*"]` to allow all worlds. Both participants must be in `Enabled-Worlds` to request, accept or open a trade, including administrator and test commands. Entering a disabled world clears related requests and cancels the active trade. An empty world list disables trading everywhere.

`Distance.Same-World` defaults to true. `Start` defaults to 4 blocks for requests and opening, and `Trading` defaults to 8 blocks during trading. Distances include height; 0 disables the corresponding distance limit. Cross-world trading requires Same-World false and both distances 0. A shared task checks actual positions every 5 ticks, and settlement rechecks the rules. Exceeding the active limit cancels the countdown, returns items, closes both menus and notifies both players.

`Safe.Close-On-Damage` defaults to false. When enabled, uncancelled positive damage cancels the trade and returns items. Trading damage immunity takes priority. Existing explicit configuration values are preserved on upgrade.

Planned: persistent trade history with transaction IDs, timestamps, participant UUIDs/names, item snapshots, currency amounts, outcomes and refund failures, plus administrator lookup and retention settings.

## Trade requests

`Trade.Request.Cooldown` defaults to 5 seconds, shared across all recipients for each sender. Only successful requests consume it; 0 disables it. `Trade.Request.Expire` independently defaults to 30 seconds. An existing request to the same player cannot be duplicated or extended. Cooldown accepts 0–86400 seconds and expiry accepts 1–86400; invalid values use defaults.

Busy players cannot send or receive new requests. Creating a trade or test session clears all incoming and outgoing requests for its participants, preserving unrelated requests and sender cooldowns. Old chat links cannot reopen these cleared requests after the trade finishes. Reload clears requests and cooldowns.

## Currencies and experience levels

Configure `Trade.Economy.Currencies` to enable the accounts used for trading:

```yaml
Trade:
  Economy:
    Enable: true
    Allow-Negative: false
    Amounts: [1000, 10000]
    Currencies:
      vault:
        Enable: true
        Provider: vault
        Name: "Coins"
      levels:
        Enable: true
        Provider: experience
        Name: "Levels"
        Amounts: [1, 5, 10, 30]
      points:
        Enable: true
        Provider: playerpoints
        Name: "Points"
        Amounts: [1, 10, 100, 1000]
      tokens:
        Enable: true
        Provider: excellenteconomy
        Currency: tokens
        Name: "Tokens"
        Amounts: [1, 10, 100, 1000]
```

The shipped configuration disables the economy master switch. Once enabled, its default currency entry is Vault. Disabled or unavailable currencies have no buttons, including in test mode. Older files without `Currencies` retain their Vault provider and existing button amounts. Each entry has a unique local ID, a provider, and a display `Name`. ExcellentEconomy also requires the ID of an existing currency in `Currency`; duplicate the entry to add more currencies. Missing plugins or incompatible APIs disable only the affected entry.

The built-in `experience` provider trades whole Minecraft levels without another plugin. A payment of 5 levels changes level 30 to 25 and preserves the experience bar progress.

Each currency supports one to four positive `Amounts`, falling back to `Economy.Amounts` when omitted. PlayerPoints requires integers up to `2147483647`; ExcellentEconomy integer currencies reject fractions too. Configure each underlying account once, including accounts already exposed through Vault.

Left-click the center divider to switch the currency being edited. Existing offers remain, and the divider lists payments for the current currency plus every nonzero offer. Both players must unconfirm before switching. Changes to any amount reset existing confirmations. Currencies are settled separately, without exchange rates or offsets between different currencies. Button appearance is configured in `View.yml`. Vault uses gold ingots, PlayerPoints uses emeralds, experience levels use experience bottles, and ExcellentEconomy uses sunflowers by default. These defaults also apply when older files omit the new sections.

Use `currency-defaults.<Provider>` for provider defaults and `currency-buttons.<currency ID>.Money` for a specific currency. The ID matches the key under `Trade.Economy.Currencies`, such as `points` or `levels`. `Money-1` through `Money-4` override individual amount buttons.

Each section supports `material`, `name`, `lore`, and `custom_model_data`. Later layers override only fields they specify: provider defaults → `button.Money` → `button.Money-1…4` → currency `Money` → currency `Money-1…4`. Existing global material settings still take priority over provider defaults; remove that field or use `@default@` to retain different materials per provider.

```yaml
currency-defaults:
  playerpoints:
    material: paper
currency-buttons:
  tokens:
    Money:
      material: diamond
      name: "&b%currency% &7| &e%amount%"
    Money-4:
      material: diamond_block
```

`%amount%` includes the amount and currency name; `%currency%` is the current name. Both sides update when switching types, and the center divider appends payment details after custom lore. Use `/gt reload` to apply configuration changes.

All balances are checked before settlement. If a provider rejects payment, items are returned and completed payments are reversed in reverse order. Failed refunds are reported to both players and logged with the currency, player UUIDs, and amount. These operations are not a cross-plugin database transaction. Process crashes and providers that mutate balances before throwing may require manual reconciliation. `/gt reload` cancels active trades before replacing currency configuration.

The adapters use the [PlayerPoints UUID API](https://github.com/Rosewood-Development/PlayerPoints/blob/master/src/main/java/org/black_ixx/playerpoints/PlayerPointsAPI.java) and [ExcellentEconomy synchronous API](https://github.com/nulli0n/ExcellentEconomy/blob/master/src/main/java/su/nightexpress/excellenteconomy/api/ExcellentEconomyAPI.java). ExcellentEconomy must expose `getAPI()`, currency-ID-based balance/deposit/withdraw methods, and currency limit methods. Legacy CoinsEngine APIs are not supported. The GoodsTrade Java 8 target does not lower the requirements of installed economy plugins.

Local tests cover settlement, compensation, and API contract doubles. Before deployment, use two players to verify mixed currencies, confirmation resets, balance changes during the countdown, cancellation, reload, and final balances on the server.

## Administrator test mode

Players with `goodstrade.command.test` can run `/gt test [test-name]`. The administrator controls both offer areas, both sets of money buttons, and both confirmation buttons, making it possible to verify negative offers, confirmation resets, and the complete countdown without a second online player.

Test mode leaves currency and levels unchanged. Items are returned when the test finishes, the menu closes, or the plugin reloads. Run this command in game.

## Item blacklist

The blacklist checks the items a player placed in their offer when they confirm it.

```yaml
Trade:
  Item-BlackList:
    Enable: true
    Lore:
      - "Soulbound"
      - "Untradeable"
    Name:
      - "Admin Tool"
    NBT:
      - "PublicBukkitValues.myplugin:soulbound"
      - "item.owner@server"
```

- A `Lore` or `Name` rule matches when the configured text appears in the item's lore or display name.
- An NBT path without `@` blocks any item containing that tag.
- `path@value` blocks the item only when the resolved NBT value matches.
- Nested paths use dots. Escape a literal dot in a key with `\.`.

## Menu customization

`View.yml` controls the background, separator, ready buttons, countdown button, cancellation state, and money buttons. `Money` sets the shared money-button style; `Money-1` through `Money-4` can override individual steps, and `%amount%` is replaced at runtime. A button section can define:

```yaml
button:
  "#":
    material: black_stained_glass_pane
    name: "&7Background"
    lore:
      - "&7"
    custom_model_data: 0
```

Materials are resolved through XMaterial where possible, which keeps names usable across a wide range of Minecraft versions. Unsupported custom model data is skipped on older servers.

## PlaceholderAPI

When PlaceholderAPI is installed, GoodsTrade provides:

```text
%goodstrade_stats%
```

It returns `true` when the player accepts trade requests and `false` when requests are disabled.

## Support

- [GitHub issues](https://github.com/polang233/GoodsTrade/issues)
- QQ group: `620224543`

If GoodsTrade fits your server, consider leaving a [⭐ GitHub Star](https://github.com/polang233/GoodsTrade).

## License

GoodsTrade is released under the [GNU General Public License v3.0](https://github.com/polang233/GoodsTrade/blob/master/LICENSE).

## Usage statistics

[![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)](https://bstats.org/plugin/bukkit/GoodsTrade/30110)
