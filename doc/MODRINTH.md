![GoodsTrade logo](https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/logo.png)

# GoodsTrade Lite

**A lightweight, confirmation-based item trading menu for Bukkit, Spigot, and Paper servers.**

GoodsTrade gives both players a shared trade menu with clearly separated offer slots. Players can review, confirm, and cancel before anything changes hands. Offers are locked once confirmed, and a final countdown leaves time to catch a mistake or a last-second item swap.

[Download the latest release](https://github.com/polang233/GoodsTrade/releases/latest) · [Source code](https://github.com/polang233/GoodsTrade/tree/lite) · [Report an issue](https://github.com/polang233/GoodsTrade/issues) · [Full English guide](https://github.com/polang233/GoodsTrade/blob/lite/doc/README_EN.md)

![Version](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/polang233/GoodsTrade/lite/.github/badges/lite-version.json)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.2-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)

![GoodsTrade menu demonstration](https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/QQ20260315-190201-HD.gif)

## Features

- Two-player confirmation before any items are exchanged
- Configurable final countdown; either side can still cancel
- Locked offers after confirmation to prevent last-second item swapping
- Optional Vault money offers with GUI amount buttons, balance checks, and confirmation resets after a price change
- Safe item returns when a menu is closed or a trade is cancelled
- Overflow items are dropped at the player's location instead of being deleted
- Requests through `/gt sendtrade <player>` or sneak-right-click
- Per-player request toggle for avoiding unwanted trade spam
- Item blacklist rules for names, lore, NBT paths, and NBT values
- Customizable menu materials, names, lore, and custom model data
- Optional movement and damage protection during a trade
- Optional PlaceholderAPI integration
- Built-in Chinese and English messages

![GoodsTrade secondary preview](https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/1773587841043.webp)

## Compatibility

- **Minecraft:** 1.12–26.2
- **Server software:** Bukkit, Spigot, and Paper
- **Java:** 8 or newer
- **Optional dependencies:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/); [Vault](https://www.spigotmc.org/resources/vault.34315/) plus a compatible economy plugin for money trading

GoodsTrade Lite does **not** currently claim Folia support.

## Installation

1. Download the latest Lite build from [GitHub Releases](https://github.com/polang233/GoodsTrade/releases/latest).
2. Place the JAR in your server's `plugins` directory.
3. Start the server once to generate the configuration files.
4. Edit the files under `plugins/GoodsTrade/` and run `/gt reload`.

## How trading works

1. Send a request with `/gt sendtrade <player>`, or sneak-right-click the player when enabled.
2. The other player clicks the chat prompt or runs `/gt accept`.
3. Both players place their items into their own side of the menu. Money buttons can be used if Vault trading is enabled.
4. Each player confirms their offer.
5. GoodsTrade locks both offers and starts the countdown. Either player can still cancel.
6. When the countdown ends, money is settled if enabled and the items are exchanged automatically.

Closing the menu or cancelling the confirmation returns the offered items. Changing a money offer resets any existing confirmation so both players must review again.

## Commands

| Command | Description |
|---|---|
| `/gt sendtrade <player>` | Send a trade request |
| `/gt accept` | Accept your only pending request |
| `/gt accept <player>` | Accept a request from a specific player |
| `/gt toggle [true\|false]` | Toggle or explicitly set incoming requests |
| `/gt trade <sender> <receiver>` | Open a trade as an administrator |
| `/gt test [virtual-player-name]` | Run a sandbox trade while controlling both sides |
| `/gt reload` | Reload configuration, menu items, blacklist rules, and language files |
| `/gt` | Show available subcommands |

Player-facing command permissions default to everyone. `trade`, `test`, and `reload` default to server operators.

## English and Chinese messages

GoodsTrade extracts every bundled translation from the JAR. Version 1.1.7 includes:

```text
plugins/GoodsTrade/lang/zh_cn.yml
plugins/GoodsTrade/lang/en_us.yml
```

The default `system` setting follows the server JVM/operating-system locale. Older configs without a `Language` key also use automatic detection. You can select a locale explicitly, then run `/gt reload`:

```yaml
Language: system
# Language: zh_cn
# Language: en_us
```

Locale filenames follow the standard i18n style, such as `zh_cn`, `en_us`, and `ja_jp`. If a locale is unavailable, GoodsTrade logs a warning and falls back to `zh_cn`.

On future updates, any new translation bundled under `lang/` is generated automatically when the server does not already have that file. Existing translations are never overwritten. The root-level `Lang.yml` from older published versions is migrated to `lang/zh_cn.yml` when needed and is not deleted.

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

- `Wait-Time` is the final confirmation countdown in seconds, capped at 64.
- `Economy.Amounts` defines one to four button steps. Left click adds the step and right click subtracts it.
- `Allow-Negative` lets an offer go below zero; a negative offer means the other player must pay.
- Changing money resets any existing confirmation. Balances are checked on every change, on confirm, and immediately before settlement.

## Administrator sandbox trade

`/gt test [virtual-player-name]` opens a sandbox menu where an administrator controls both offer areas, both money buttons, and both confirmation buttons. It never calls Vault or exchanges items. Anything placed in the menu is returned when the test completes, closes, or is interrupted by a reload. The command cannot be run from the console.

## Item blacklist

Blacklist checks run when a player confirms an offer. Rules can match visible item text or NBT data:

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

An NBT path by itself blocks items containing that tag. Use `path@value` when the value must match as well. Nested paths use dots; escape a literal dot in a key with `\.`.

## Menu customization

`View.yml` can override the background, separator, ready buttons, countdown button, cancellation state, and money buttons. `Money` sets the shared money-button style; `Money-1` through `Money-4` can override individual steps, and `%amount%` is replaced at runtime.

```yaml
button:
  "#":
    material: black_stained_glass_pane
    name: " "
    lore:
      - "&7"
    custom_model_data: 0
```

Materials are resolved through XMaterial for broad version compatibility. Unsupported custom model data is skipped on older servers.

## PlaceholderAPI

With PlaceholderAPI installed, `%goodstrade_stats%` returns `true` when the selected player currently accepts trade requests and `false` when requests are disabled.

## Metrics and support

GoodsTrade uses bStats for anonymous usage statistics. Server owners can opt out through `plugins/bStats/config.yml`.

![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)

- [GitHub issues](https://github.com/polang233/GoodsTrade/issues)
- QQ group: `620224543`

中国用户请加 QQ 群 620224543，反馈最快。其他用户请走 GitHub Issues。

If GoodsTrade fits your server, consider leaving a [⭐ GitHub star](https://github.com/polang233/GoodsTrade).

GoodsTrade is open source under the [GNU GPL v3](https://github.com/polang233/GoodsTrade/blob/lite/LICENSE).
