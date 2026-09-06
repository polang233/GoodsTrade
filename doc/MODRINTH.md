**Exchange items, money, and experience levels through a shared chest menu.**

GoodsTrade gives both players separate offer areas so they can review the exchange before confirming. Once both players confirm, the plugin locks the offers and starts a configurable countdown. Closing or cancelling the trade returns the offered items.

## Features

- Trade items with another player through one inventory menu.
- Add offers in Vault currency, PlayerPoints, ExcellentEconomy currencies, or Minecraft experience levels. One trade can include several currencies.
- Require confirmation from both players and reset confirmation when an offer changes.
- Send requests with `/gt sendtrade <player>`, or sneak-right-click when that trigger is enabled.
- Let players turn incoming trade requests on or off.
- Configure item restrictions using names, lore, NBT paths, and values.
- Customize menu materials, names, lore, amount buttons, and currency-specific button appearances.
- Use built-in English or Chinese messages, with configurable language files.
- Test the trade menu as an administrator without needing a second player.

## Trading in game

Players put items on their own side of the menu and review the other player's offer. Confirmation and the countdown are visible to both players.

![GoodsTrade item exchange and confirmation demonstration](https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/QQ20260315-190201-HD.gif)

![GoodsTrade item offer areas](https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/1773587841043.webp)

## Requirements

- **Minecraft:** 1.12–26.2.
- **Server software:** Bukkit, Spigot, or Paper. Folia support is not currently claimed.
- **Java:** 8 or newer.
- **Item trading:** no economy plugin is required.
- **Currency trading:** install the provider you want to use. Vault also needs a compatible economy plugin; PlayerPoints and ExcellentEconomy use their own balances.
- **Experience levels:** no economy plugin is required.
- **PlaceholderAPI:** optional, for the `%goodstrade_stats%` placeholder.

## Installation

1. Download the plugin version matching your server and put the JAR in `plugins/`.
2. Start the server to create `plugins/GoodsTrade/config.yml`, `View.yml`, and the language files.
3. Enable the currencies and menu options you want to use.
4. Run `/gt reload` after editing the configuration.

Missing or unavailable economy providers are skipped, so item trading remains available.

## Start a trade

1. Run `/gt sendtrade <player>`, or sneak-right-click the player.
2. The recipient accepts the chat invitation or runs `/gt accept`.
3. Put items into your offer area and use the amount buttons for enabled currencies.
4. Both players confirm their offers.
5. After the countdown, the plugin settles the currency offers and exchanges the items.

Either player can cancel before completion. Items that do not fit in the owner's inventory are dropped at that player's location.

## Commands and permissions

Normal player commands are enabled by default:

- `/gt`: show help. Permission: `goodstrade.command`.
- `/gt sendtrade <player>`: send a request. Permission: `goodstrade.command.sendtrade`.
- `/gt accept [player]`: accept a request. Permission: `goodstrade.command.accept`.
- `/gt toggle [true|false]`: control incoming requests. Permission: `goodstrade.command.toggle`.

Administrator commands default to operators:

- `/gt trade <sender> <receiver>`: start a trade between two players. Permission: `goodstrade.command.trade`.
- `/gt test [test-name]`: control both sides of a test trade. Permission: `goodstrade.command.test`.
- `/gt reload`: reload configuration and messages. Permission: `goodstrade.command.reload`.

The base `goodstrade.command` permission is required for all commands.

## Currencies and menu settings

Only Vault is enabled in the default currency configuration. Enable other providers under `Trade.Economy.Currencies`. Each currency supports a display name and one to four amount steps.

Left-click an amount button to increase the offer, or right-click to decrease it. Left-click the center divider to change the selected currency; existing offers remain visible in the divider. Both players must cancel confirmation before switching currencies.

Currencies are settled separately and are not converted into one another. Do not configure the same underlying account twice through different providers. Experience offers use whole levels and preserve experience bar progress.

`View.yml` controls menu appearances. `currency-defaults` sets provider defaults, while `currency-buttons` overrides a specific currency's buttons.

The [configuration guide](https://github.com/polang233/GoodsTrade/blob/master/doc/README_EN.md#currencies-and-experience-levels) contains complete examples.

## Language

The default language follows the server's system locale, with Simplified Chinese as the fallback. Set `Language: en_us` or `Language: zh_cn` in `config.yml` to choose explicitly, then run `/gt reload`.

Existing language files are preserved. Help text and player messages can be edited under `plugins/GoodsTrade/lang/`.

## Support

Report problems on [GitHub Issues](https://github.com/polang233/GoodsTrade/issues). Chinese-speaking users can also use QQ group `620224543`.

GoodsTrade is available under the [GNU GPL v3](https://github.com/polang233/GoodsTrade/blob/master/LICENSE). If you find it useful, a [⭐ GitHub Star](https://github.com/polang233/GoodsTrade) is appreciated.

## Usage statistics

[![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)](https://bstats.org/plugin/bukkit/GoodsTrade/30110)
