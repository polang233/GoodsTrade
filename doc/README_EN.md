<p align="center">
  <img src="../img/logo.png" alt="GoodsTrade Logo" width="220">
</p>

# GoodsTrade Lite

GoodsTrade is a lightweight, inventory-based trading plugin for Minecraft servers. It gives both players a shared trade menu, locks offers once they are confirmed, and waits through a final countdown before exchanging the items.

> 中文文档请查看 [README.md](../README.md)。

<p align="center">
  <img src="../img/QQ20260315-190201-HD.gif" alt="GoodsTrade trade menu demonstration">
</p>

![Version](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/polang233/GoodsTrade/refs/heads/lite/.github/badges/lite-version.json)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.1-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)

## Why use GoodsTrade?

- **Two-player confirmation:** the trade only completes after both players approve their offers.
- **Final countdown:** either side can stop the confirmation before the exchange is committed.
- **Locked offers:** confirmed players cannot quietly swap items at the last moment.
- **Vault money offers:** configurable GUI buttons add or subtract money, validate both balances, and reset stale confirmations after a change.
- **Safe returns:** cancelling or closing the menu returns offered items; overflow is dropped at the player's location instead of disappearing.
- **Quick requests:** players can use a command or sneak-right-click another player.
- **Trade preferences:** each player can disable incoming requests when they want some peace and quiet.
- **Item rules:** block items by display name, lore text, an NBT path, or an NBT path/value pair.
- **Custom menus:** change button materials, names, lore, and custom model data in `View.yml`.
- **Optional protection:** prevent damage or block movement while a trade is open.
- **PlaceholderAPI hook:** read whether a player currently accepts trade requests.
- **Chinese and English messages:** switch languages from `config.yml` and edit either language file freely.

## Requirements

- Minecraft `1.12` through `26.1`
- Bukkit, Spigot, or Paper
- Java 8 or newer
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is optional
- [Vault](https://www.spigotmc.org/resources/vault.34315/) plus a Vault-compatible economy plugin is required only for money trading

GoodsTrade Lite does **not** currently claim Folia support.

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
| `/gt test [virtual-player-name]` | `goodstrade.command.test` | Open a sandbox trade with a nonexistent virtual player |
| `/gt reload` | `goodstrade.command.reload` | Reload configuration, menu items, blacklist rules, and language messages |
| `/gt` | `goodstrade.command` | Show available subcommands |

The player-facing command permissions default to everyone. `trade` and `reload` default to server operators.

## Language selection

New installations extract every bundled translation from the JAR. Version 1.1.7 includes:

```text
plugins/GoodsTrade/lang/zh_cn.yml
plugins/GoodsTrade/lang/en_us.yml
```

By default, GoodsTrade follows the server JVM/operating-system locale. Older configs without a `Language` key behave the same way. If no matching translation is available, the console explains the fallback and GoodsTrade uses Simplified Chinese.

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
  Wait-Time: 5
  Economy:
    Enable: true
    Allow-Negative: false
    Amounts:
      - 1000
      - 10000
  Triggers:
    Shift-Right-Click: true
  Safe:
    Damage: false
    Move: false
```

- `Wait-Time` controls the final confirmation countdown in seconds and is capped at 64.
- `Economy.Amounts` defines one to four button steps. Left click adds the step and right click subtracts it.
- `Allow-Negative` lets an offer cross below zero; a negative offer means the other player must pay. The divider always shows each player's resulting payment obligation.
- Balance checks run on every amount change, on confirmation, and immediately before settlement. Changing money resets any existing confirmation so both players must review again.
- `Shift-Right-Click` enables the quick request gesture.
- `Safe.Damage` cancels damage against players who are currently trading.
- `Safe.Move` stops block-to-block movement while the trade menu is open.

## Administrator sandbox trade

Players with `goodstrade.command.test` can run `/gt test [virtual-player-name]`. The administrator controls both offer areas, both sets of money buttons, and both confirmation buttons, making it possible to verify negative offers, confirmation resets, and the complete countdown without a second online player.

Sandbox trades never call Vault or exchange items. Anything placed on either side is returned to the administrator when the test completes, closes, or is interrupted by a reload. The command cannot be run from the console.

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

## Metrics

GoodsTrade uses bStats to collect anonymous usage statistics. Server owners can disable bStats globally in `plugins/bStats/config.yml`.

![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)

## Support

- [GitHub issues](https://github.com/polang233/GoodsTrade/issues)
- QQ group: `620224543`

If GoodsTrade fits your server, consider leaving a [GitHub star](https://github.com/polang233/GoodsTrade).

## License

GoodsTrade is released under the [GNU General Public License v3.0](https://github.com/polang233/GoodsTrade/blob/lite/LICENSE).
