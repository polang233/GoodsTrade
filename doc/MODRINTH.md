![GoodsTrade logo](https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/logo.png)

# GoodsTrade Lite

**A lightweight, confirmation-based item trading menu for Bukkit, Spigot, and Paper servers.**

GoodsTrade gives players a clear two-sided inventory where they can review each other's offers before anything changes hands. Both players must confirm, offers are locked during confirmation, and a final countdown leaves time to catch a mistake before the exchange completes.

[Download the latest release](https://github.com/polang233/GoodsTrade/releases/latest) · [Source code](https://github.com/polang233/GoodsTrade/tree/lite) · [Report an issue](https://github.com/polang233/GoodsTrade/issues) · [Full English guide](https://github.com/polang233/GoodsTrade/blob/lite/doc/README_EN.md)

![GoodsTrade menu demonstration](https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/QQ20260315-190201-HD.gif)

## Features

- A shared two-player trade menu with clearly separated offer slots
- Confirmation from both players before any items are exchanged
- Configurable final countdown with a chance to cancel
- Locked offers after confirmation to prevent last-second item swapping
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

- **Minecraft:** 1.12–26.1
- **Server software:** Bukkit, Spigot, and Paper
- **Java:** 8 or newer
- **Optional dependency:** PlaceholderAPI

GoodsTrade Lite does **not** currently claim Folia support.

## How trading works

1. Send a request with `/gt sendtrade <player>`, or sneak-right-click the player when enabled.
2. The other player clicks the chat prompt or runs `/gt accept`.
3. Both players place their items into their own side of the menu.
4. Each player confirms their offer.
5. GoodsTrade locks both offers and starts the countdown.
6. When the countdown ends, the items are exchanged automatically.

Closing the menu or cancelling the confirmation returns the offered items.

## Commands

| Command | Description |
|---|---|
| `/gt sendtrade <player>` | Send a trade request |
| `/gt accept` | Accept your only pending request |
| `/gt accept <player>` | Accept a request from a specific player |
| `/gt toggle [true\|false]` | Toggle or explicitly set incoming requests |
| `/gt trade <sender> <receiver>` | Open a trade as an administrator |
| `/gt reload` | Reload configuration, menu items, blacklist rules, and language files |

## English and Chinese messages

GoodsTrade extracts every bundled translation from the JAR. Version 1.1.5 includes:

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

An NBT path by itself blocks items containing that tag. Use `path@value` when the value must match as well.

## Menu customization

`View.yml` can override the default menu buttons. Materials are resolved through XMaterial for broad version compatibility.

```yaml
button:
  "#":
    material: black_stained_glass_pane
    name: " "
    lore:
      - "&7"
    custom_model_data: 0
```

## PlaceholderAPI

With PlaceholderAPI installed, `%goodstrade_stats%` returns whether the selected player currently accepts trade requests.

## Metrics and support

GoodsTrade uses bStats for anonymous usage statistics. Server owners can opt out through `plugins/bStats/config.yml`.

![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)

- [GitHub issues](https://github.com/polang233/GoodsTrade/issues)
- QQ group: `620224543`

GoodsTrade is open source under the [GNU GPL v3](https://github.com/polang233/GoodsTrade/blob/lite/LICENSE).
