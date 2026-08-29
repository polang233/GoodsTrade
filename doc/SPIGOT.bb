[CENTER]
[SIZE=7][B]GoodsTrade[/B]
[/SIZE]
[SIZE=4]A lightweight, confirmation-based item trading plugin for Minecraft servers[/SIZE]
[IMG]https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/logo-96x96.jpg[/IMG][/CENTER]
[IMG]https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/演示.gif[/IMG]


[SIZE=5][B]Overview[/B][/SIZE]

GoodsTrade is a lightweight item trading plugin for Minecraft servers.

It gives both players a shared trade menu with clearly separated offer slots. Players can review, confirm, and cancel before anything changes hands. Offers are locked once confirmed, and a final countdown leaves time to catch a mistake or a last-second item swap.


[IMG]https://raw.githubusercontent.com/polang233/GoodsTrade/lite/img/1773587841043.webp[/IMG]


[SIZE=5][B]Features[/B][/SIZE]

[LIST]
[*][B]Two-player confirmation[/B] - The trade only completes after both players approve their offers.
[*][B]Final countdown[/B] - Either side can still stop the confirmation before the exchange is committed.
[*][B]Locked offers[/B] - Confirmed players cannot quietly swap items at the last moment.
[*][B]Visual GUI[/B] - Simple and intuitive GUI trading interface. No complicated commands are required.
[*][B]Quick trade request[/B] - Players can start a trade with a command, or by sneaking and right-clicking another player if enabled in the configuration.
[*][B]Vault money offers[/B] - Configurable GUI buttons add or subtract money, validate both balances, and reset stale confirmations after a change.
[*][B]Safe returns[/B] - Cancelling or closing the menu returns offered items. Overflow is dropped at the player's location instead of disappearing.
[*][B]Trade preferences[/B] - Each player can disable incoming requests when they want some peace and quiet.
[*][B]Item rules[/B] - Block items by display name, lore text, an NBT path, or an NBT path/value pair.
[*][B]Custom menus[/B] - Change button materials, names, lore, and custom model data in View.yml.
[*][B]Optional protection[/B] - Prevent damage or block movement while a trade is open.
[*][B]PlaceholderAPI hook[/B] - Read whether a player currently accepts trade requests.
[*][B]Chinese and English messages[/B] - Switch languages from config.yml and edit either language file freely.
[/LIST]


[SIZE=5][B]Plugin Information[/B][/SIZE]

[LIST]
[*][B]Plugin Version:[/B] [URL='https://github.com/polang233/GoodsTrade/tree/lite'][IMG]https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/polang233/GoodsTrade/lite/.github/badges/lite-version.json[/IMG][/URL]
[*][B]Supported Minecraft Versions:[/B] 1.12 - 26.2
[*][B]Supported Server Software:[/B] Bukkit, Spigot, Paper
[*][B]Java Requirement:[/B] Java 8 or above
[*][B]Dependencies:[/B] Optional
[*][B]Optional Dependency:[/B] [URL='https://www.spigotmc.org/resources/placeholderapi.6245/']PlaceholderAPI[/URL]
[*][B]Optional Dependency:[/B] [URL='https://www.spigotmc.org/resources/vault.34315/']Vault[/URL] plus a Vault-compatible economy plugin, required only for money trading
[*][B]Folia:[/B] GoodsTrade Lite does not currently claim Folia support.
[/LIST]


[SIZE=5][B]Installation[/B][/SIZE]

[LIST=1]
[*]Download the latest Lite build from [URL='https://github.com/polang233/GoodsTrade/releases/latest']GitHub Releases[/URL].
[*]Place the JAR in your server's plugins directory.
[*]Start the server once to generate the configuration files.
[*]Edit the files under plugins/GoodsTrade/ and run [B]/gt reload[/B].
[/LIST]


[SIZE=5][B]Commands[/B][/SIZE]

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

[B]/gt test [virtual-player-name][/B]
Permission: goodstrade.command.test
Open a sandbox trade with a nonexistent virtual player. The administrator controls both sides; no real money or items are exchanged.

[B]/gt reload[/B]
Permission: goodstrade.command.reload
Reload configuration, menu items, blacklist rules, and language messages.

[B]/gt[/B]
Permission: goodstrade.command
Show command help.


[SIZE=5][B]Permissions[/B][/SIZE]

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
Allows opening a sandbox trade with a virtual player to test the menu and full confirmation flow.

[B]goodstrade.command.reload[/B]
Default: OP
Allows reloading the plugin configuration.


[SIZE=5][B]Configuration[/B][/SIZE]

The configuration files are located at:

[B]plugins/GoodsTrade/config.yml[/B]

[B]plugins/GoodsTrade/View.yml[/B]

[B]plugins/GoodsTrade/lang/zh_cn.yml[/B]

[B]plugins/GoodsTrade/lang/en_us.yml[/B]


[SIZE=4][B]Language selection[/B][/SIZE]

New installations extract every bundled translation from the JAR. Version 1.1.7 includes zh_cn and en_us.

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


[SIZE=4][B]Main configuration[/B][/SIZE]

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
[*][B]Wait-Time[/B] controls the final confirmation countdown in seconds and is capped at 64.
[*][B]Economy.Amounts[/B] defines one to four button steps. Left click adds the step and right click subtracts it.
[*][B]Allow-Negative[/B] lets an offer cross below zero; a negative offer means the other player must pay. The divider always shows each player's resulting payment obligation.
[*]Balance checks run on every amount change, on confirmation, and immediately before settlement. Changing money resets any existing confirmation so both players must review again.
[*][B]Shift-Right-Click[/B] enables the quick request gesture.
[*][B]Safe.Damage[/B] cancels damage against players who are currently trading.
[*][B]Safe.Move[/B] stops block-to-block movement while the trade menu is open.
[/LIST]


[SIZE=4][B]Item blacklist[/B][/SIZE]

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


[SIZE=4][B]Menu customization[/B][/SIZE]

View.yml controls the background, separator, ready buttons, countdown button, cancellation state, and money buttons. Money sets the shared money-button style; Money-1 through Money-4 can override individual steps, and %amount% is replaced at runtime.

Materials are resolved through XMaterial where possible, which keeps names usable across a wide range of Minecraft versions. Unsupported custom model data is skipped on older servers.


[SIZE=5][B]Administrator sandbox trade[/B][/SIZE]

Players with goodstrade.command.test can run [B]/gt test [virtual-player-name][/B]. The administrator controls both offer areas, both sets of money buttons, and both confirmation buttons, making it possible to verify negative offers, confirmation resets, and the complete countdown without a second online player.

Sandbox trades never call Vault or exchange items. Anything placed on either side is returned to the administrator when the test completes, closes, or is interrupted by a reload. The command cannot be run from the console.


[SIZE=5][B]How to Use[/B][/SIZE]

[SIZE=4][B]Basic Trading Process[/B][/SIZE]

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


[SIZE=4][B]Trading Notes[/B][/SIZE]

[LIST]
[*]After confirming, trade items are locked and cannot be changed.
[*]When both players confirm, a 5-second countdown starts by default. This can be changed in the configuration.
[*]The trade can still be cancelled during the countdown.
[*]If the trade is cancelled or the GUI is closed, items are automatically returned.
[*]Changing a money offer resets any existing confirmation. Both players must review and confirm again.
[/LIST]


[SIZE=5][B]PlaceholderAPI[/B][/SIZE]

When PlaceholderAPI is installed, GoodsTrade provides:

[B]%goodstrade_stats%[/B]

It returns true when the player accepts trade requests and false when requests are disabled.


[SIZE=5][B]Planned Features[/B][/SIZE]

[LIST]
[*][B]Done:[/B] Item blacklist system
[*][B]Done:[/B] Improved permission module
[*][B]Done:[/B] Custom GUI materials, lore, and display options
[*][B]Done:[/B] Vault money trading
[*]Support for levels and other currencies
[*]Trade history records
[*]Custom trade requirements configurable by the server
[*]Trade cooldown settings
[*]Suspicious trade warning system
[*]Cancel trades when players are too far away
[/LIST]


[SIZE=5][B]Known Issues[/B][/SIZE]

[LIST]
[*]The GUI title may look strange when player names are too long.
[/LIST]


[SIZE=5][B]Support and Feedback[/B][/SIZE]

If you encounter any issues or have feature suggestions, feel free to contact me:

[LIST]
[*][B]Issues:[/B] [URL]https://github.com/polang233/GoodsTrade/issues[/URL]
中国用户请加 QQ 群 620224543，反馈最快。其他用户请走 GitHub Issues。
[/LIST]


GoodsTrade is released under the [URL='https://github.com/polang233/GoodsTrade/blob/lite/LICENSE']GNU General Public License v3.0[/URL].


[CENTER]
[IMG]https://bstats.org/signatures/bukkit/GoodsTrade.svg[/IMG]

[SIZE=4][B][URL='https://github.com/polang233/GoodsTrade']⭐ this plugin is free! please consider giving it a star on GitHub! ⭐[/URL][/B][/SIZE]
[/CENTER]