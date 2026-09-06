[B]GoodsTrade[/B]

Trade items, currency, and experience levels through a shared chest menu.

[IMG]https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/演示.gif[/IMG]

[B]Overview[/B]

GoodsTrade lets players exchange items, currency, and experience levels in Minecraft.

It gives both players a shared trade menu with clearly separated offer slots. Players can review, confirm, and cancel before anything changes hands. Offers are locked once confirmed, and a final countdown leaves time to catch a mistake or a last-second item swap.

[IMG]https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/1773587841043.webp[/IMG]

[B]Features[/B]

[LIST]
[*][B]Two-player confirmation[/B] - The trade only completes after both players approve their offers.
[*][B]Final countdown[/B] - Either side can still stop the confirmation before the exchange is committed.
[*][B]Locked offers[/B] - Confirmed players cannot quietly swap items at the last moment.
[*][B]Visual GUI[/B] - Place items and set amounts in a shared chest menu.
[*][B]Quick trade request[/B] - Players can start a trade with a command, or by sneaking and right-clicking another player if enabled in the configuration.
[*][B]Multiple currencies and levels[/B] - Use Vault, PlayerPoints, ExcellentEconomy, or Minecraft experience levels. Both balances are checked before payment.
[*][B]Safe returns[/B] - Cancelling or closing the menu returns offered items. Overflow is dropped at the player's location instead of disappearing.
[*][B]Trade preferences[/B] - Each player can enable or disable incoming requests.
[*][B]Item rules[/B] - Block items by display name, lore text, an NBT path, or an NBT path/value pair.
[*][B]Custom menus[/B] - Change button materials, names, lore, and custom model data in View.yml.
[*][B]Optional protection[/B] - Prevent damage or block movement while a trade is open.
[*][B]PlaceholderAPI hook[/B] - Read whether a player currently accepts trade requests.
[*][B]Chinese and English messages[/B] - Switch languages from config.yml and edit either language file freely.
[/LIST]

[B]Plugin Information[/B]

[LIST]
[*][B]Plugin Version:[/B] [URL='https://github.com/polang233/GoodsTrade/releases/latest'][IMG]https://img.shields.io/github/v/release/polang233/GoodsTrade?label=Lite%20轻量版本&color=2ea44f[/IMG][/URL]
[*][B]Supported Minecraft Versions:[/B] 1.12 - 26.2
[*][B]Supported Server Software:[/B] Bukkit, Spigot, Paper
[*][B]Java Requirement:[/B] Java 8 or above
[*][B]Dependencies:[/B] Optional
[*][B]Optional Dependency:[/B] [URL='https://www.spigotmc.org/resources/placeholderapi.6245/']PlaceholderAPI[/URL]
[*][B]Optional Dependency:[/B] [URL='https://www.spigotmc.org/resources/vault.34315/']Vault[/URL] plus a Vault-compatible economy plugin, required only for money trading
[*][B]Folia:[/B] GoodsTrade Lite does not currently claim Folia support.
[/LIST]

[B]Installation[/B]

[LIST=1]
[*]Download the latest Lite build from [URL='https://github.com/polang233/GoodsTrade/releases/latest']GitHub Releases[/URL].
[*]Place the JAR in your server's plugins directory.
[*]Start the server once to generate the configuration files.
[*]Edit the files under plugins/GoodsTrade/ and run [B]/gt reload[/B].
[/LIST]

[B]Commands[/B]

[B]/gt sendtrade [player][/B]
Permission: goodstrade.command.sendtrade
Send a trade request to a specific player.

[B]/gt accept[/B]
Permission: goodstrade.command.accept
Accept the only pending trade request.

[B]/gt accept [player][/B]
Permission: goodstrade.command.accept
Accept a trade request from a specific player.

[B]/gt toggle [true/false][/B]
Permission: goodstrade.command.toggle
Enable, disable, or toggle receiving trade requests. Leave out the value to switch the current setting.

[B]/gt trade [sender] [receiver][/B]
Permission: goodstrade.command.trade
Force two specified players to start a trade.

[B]/gt test [test-name][/B]
Permission: goodstrade.command.test
Open test mode with a test player name. The administrator controls both sides; no real money or items are exchanged.

[B]/gt reload[/B]
Permission: goodstrade.command.reload
Reload configuration, menu items, blacklist rules, and language messages.

[B]/gt[/B]
Permission: goodstrade.command
Show command help.

[B]Permissions[/B]

[B]goodstrade.command[/B]
Default: true
Allows the player to use GoodsTrade commands. Without this permission, no commands can be used.

[B]goodstrade.command.sendtrade[/B]
Default: true
Allows the player to send trade requests to other players.

[B]goodstrade.command.accept[/B]
Default: true
Allows the player to accept trade requests.

[B]goodstrade.command.toggle[/B]
Default: true
Allows the player to enable or disable receiving trade requests.

[B]goodstrade.command.trade[/B]
Default: OP
Allows forcing two players to start a trade.

[B]goodstrade.command.test[/B]
Default: OP
Allows opening test mode to test the menu and full confirmation flow.

[B]goodstrade.command.reload[/B]
Default: OP
Allows reloading the plugin configuration.

[B]Configuration[/B]

The configuration files are located at:

[B]plugins/GoodsTrade/config.yml[/B]

[B]plugins/GoodsTrade/View.yml[/B]

[B]plugins/GoodsTrade/lang/zh_cn.yml[/B]

[B]plugins/GoodsTrade/lang/en_us.yml[/B]

[B]Language selection[/B]

New installations extract every bundled translation from the JAR. Version 1.1.9 includes zh_cn and en_us.

By default, GoodsTrade follows the server JVM/operating-system locale. Older configs without a Language key behave the same way. If no matching translation is available, the console explains the fallback and GoodsTrade uses Simplified Chinese.

You can also choose a locale explicitly in config.yml:

[QUOTE]
[FONT=Courier New]Language: system
# Language: zh_cn
# Language: en_us[/FONT]
[/QUOTE]

Run [B]/gt reload[/B] after changing the value. Locale names follow the common Minecraft/i18n format, such as zh_cn, en_us, and ja_jp.

GoodsTrade scans the complete lang/ folder inside each new JAR and extracts any translation that is missing on disk. Existing files are never overwritten, so local edits are preserved while newly bundled languages appear automatically.

When upgrading from an older published version, the root-level Lang.yml is migrated to lang/zh_cn.yml when needed. The original file is left untouched.

The command list shown by [B]/gt[/B] reads its layout and descriptions from command.help-entry and command.description in the active language file.

[B]Main configuration[/B]

[QUOTE]
[FONT=Courier New]Language: en_us

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
    Move: false[/FONT]
[/QUOTE]

[LIST]
[*][B]Wait-Time[/B] controls the final confirmation countdown in seconds, from 0 to 64. Set 0 to skip the countdown.
[*][B]Economy.Amounts[/B] defines one to four button steps. Left click adds the step and right click subtracts it.
[*][B]Allow-Negative[/B] lets an offer cross below zero; a negative offer means the other player must pay. The divider always shows each player's resulting payment obligation.
[*]Balance checks run on every amount change, on confirmation, and immediately before settlement. Changing money resets any existing confirmation so both players must review again.
[*][B]Shift-Right-Click[/B] enables the quick request gesture.
[*][B]Safe.Damage[/B] cancels damage against players who are currently trading.
[*][B]Safe.Move[/B] stops block-to-block movement while the trade menu is open.
[/LIST]

[B]Item blacklist[/B]

The blacklist checks the items a player placed in their offer when they confirm it.

[QUOTE]
[FONT=Courier New]Trade:
  Item-BlackList:
    Enable: true
    Lore:
      - "Soulbound"
      - "Untradeable"
    Name:
      - "Admin Tool"
    NBT:
      - "PublicBukkitValues.myplugin:soulbound"
      - "item.owner@server"[/FONT]
[/QUOTE]

[LIST]
[*]A Lore or Name rule matches when the configured text appears in the item's lore or display name.
[*]An NBT path without @ blocks any item containing that tag.
[*]path@value blocks the item only when the resolved NBT value matches.
[*]Nested paths use dots. Escape a literal dot in a key with \.
[/LIST]

[B]Menu customization[/B]

View.yml controls the background, separator, ready buttons, countdown button, cancellation state, and money buttons. Money sets the shared money-button style; Money-1 through Money-4 can override individual steps, and %amount% is replaced at runtime.

Materials are resolved through XMaterial where possible, which keeps names usable across a wide range of Minecraft versions. Unsupported custom model data is skipped on older servers.

[B]Currencies and experience levels[/B]

Set Trade.Economy.Currencies to enable vault, playerpoints, excellenteconomy, or experience. Each entry supports a display name and one to four amount buttons. ExcellentEconomy also requires its currency ID. Experience trades whole levels and preserves experience bar progress.

Left-click the center divider to switch types. Existing amounts remain, and every payment appears in the divider. Item or amount changes require another confirmation. Only Vault is enabled by default.

[URL='https://github.com/polang233/GoodsTrade/blob/master/doc/README_EN.md#currencies-and-experience-levels']Full configuration examples[/URL]

[B]Administrator test mode[/B]

Players with goodstrade.command.test can run [B]/gt test [test-name][/B]. The administrator controls both offer areas, both sets of money buttons, and both confirmation buttons, making it possible to verify negative offers, confirmation resets, and the complete countdown without a second online player.

Test mode leaves currency and levels unchanged. Items are returned when the test finishes, the menu closes, or the plugin reloads. Run this command in game.

[B]How to Use[/B]

[B]Basic Trading Process[/B]

[LIST=1]
[*][B]Start a trade[/B]

Use the command:

[B]/gt sendtrade [player][/B]

Or sneak and right-click a player if this feature is enabled in the configuration.

[*][B]Accept the trade[/B]

Click the [B][Click to accept][/B] message in chat, or use:

[B]/gt accept[/B]

[*][B]Place items[/B]

Put the items you want to trade into your side of the trade GUI. Money buttons can be used if Vault trading is enabled.

[*][B]Confirm the trade[/B]

Click the confirmation button. When both players confirm, the countdown starts.

[*][B]Complete the trade[/B]

After the countdown ends, money is settled if enabled and the items are exchanged automatically.
[/LIST]

[B]Trading Notes[/B]

[LIST]
[*]After confirming, trade items are locked and cannot be changed.
[*]When both players confirm, a 5-second countdown starts by default. This can be changed in the configuration.
[*]The trade can still be cancelled during the countdown.
[*]If the trade is cancelled or the GUI is closed, items are automatically returned.
[*]Changing a money offer resets any existing confirmation. Both players must review and confirm again.
[/LIST]

[B]PlaceholderAPI[/B]

When PlaceholderAPI is installed, GoodsTrade provides:

[B]%goodstrade_stats%[/B]

It returns true when the player accepts trade requests and false when requests are disabled.

[B]Planned Features[/B]

[LIST]
[*][B]Done:[/B] Item blacklist system
[*][B]Done:[/B] Improved permission module
[*][B]Done:[/B] Custom GUI materials, lore, and display options
[*][B]Done:[/B] Vault money trading
[*]Trade history records
[*]Custom trade requirements configurable by the server
[*]Trade cooldown settings
[*]Suspicious trade warning system
[*]Cancel trades when players are too far away
[/LIST]

[B]Known Issues[/B]

[LIST]
[*]The GUI title may look strange when player names are too long.
[/LIST]

[B]Support and Feedback[/B]

If you encounter any issues or have feature suggestions, feel free to contact me:

[LIST]
[*][B]QQ Group:[/B] 620224543
[*][B]Issues:[/B] [URL]https://github.com/polang233/GoodsTrade/issues[/URL]
[/LIST]

中国用户请加 QQ 群 620224543，反馈最快。其他用户请走 GitHub Issues。

GoodsTrade is released under the [URL='https://github.com/polang233/GoodsTrade/blob/master/LICENSE']GNU General Public License v3.0[/URL].

If GoodsTrade fits your server, consider leaving a [URL=https://github.com/polang233/GoodsTrade]⭐ GitHub Star[/URL].

[B]Usage statistics[/B]

[URL=https://bstats.org/plugin/bukkit/GoodsTrade/30110][IMG]https://bstats.org/signatures/bukkit/GoodsTrade.svg[/IMG][/URL]
