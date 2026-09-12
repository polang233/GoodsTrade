# GoodsTrade

**通过箱子界面交换物品、货币和经验等级，双方确认后倒计时结算。支持 Bukkit、Spigot 和 Paper。**

[English](doc/README_EN.md) · [使用反馈](https://github.com/polang233/GoodsTrade/issues)

![Version](https://img.shields.io/github/v/release/polang233/GoodsTrade?label=Version&color=2ea44f)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.2-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)
[![Powered by OrcaRouter](https://img.shields.io/badge/Powered_by-OrcaRouter-2563eb)](https://www.orcarouter.ai/ref/ref_1095590f527d3ae7ae2f)

## 下载与发布平台

[![GitHub Releases](https://img.shields.io/badge/GitHub-Releases-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/polang233/GoodsTrade/releases)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download-1BD96A?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/plugin/goodstrade)
[![MineBBS](https://img.shields.io/badge/MineBBS-Download-2196F3?style=for-the-badge)](https://www.minebbs.com/resources/goodstrade.15705/)

## 交易界面

![双方确认与交易倒计时](img/QQ20260315-190201-HD.gif)
![双方物品报价界面](img/1773587841043.webp)

---

## 简介

双方在同一个箱子界面中放入物品、设置支付数量，确认后开始倒计时。交易结束前可以取消，关闭界面会返还物品。

### 特性

- 🛡️ **安全保护**：可设置交易期间免疫伤害、限制移动；背包放不下的返还物品会掉落在脚下
- 🎯 **可视化界面**：通过箱子界面操作，也可蹲下右键玩家发送请求
- ⏱️ **确认机制**：双方确认后开始倒计时，期间可以取消并重新调整
- 🔒 **物品锁定**：确认后双方无法更改交易物品，防止受骗
- 💰 **多货币交易**：支持 Vault、PlayerPoints、ExcellentEconomy 和经验等级，余额不足时无法报价或结算，改价会重置已有确认
- ⚙️ **可配置**：支持自定义等待时间、触发方式等

### 前置要求（可选）

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- 货币交易可选接入 [Vault](https://www.spigotmc.org/resources/vault.34315/) 经济服务、PlayerPoints 或 ExcellentEconomy；未安装时仍可正常交易物品

## 命令

| 命令                        | 权限                             | 说明          |
|---------------------------|--------------------------------|-------------|
| `/gt sendtrade [玩家名]`     | `goodstrade.command.sendtrade` | 向指定玩家发起交易   |
| `/gt toggle (true/false)` | `goodstrade.command.toggle`    | 更改是否接受交易状态  |
| `/gt trade [发起者] [接收者]`   | `goodstrade.command.trade`     | 让设定两个玩家进行交易 |
| `/gt accept`              | `goodstrade.command.accept`    | 接受当前交易请求    |
| `/gt accept [玩家名]`        | `goodstrade.command.accept`    | 接受指定玩家的交易请求 |
| `/gt test [测试名称]`       | `goodstrade.command.test`      | 与虚拟玩家进行测试交易 |
| `/gt reload`              | `goodstrade.command.reload`    | 重载插件配置文件    |
| `/gt`                     | `goodstrade.command`           | 查询命令帮助      |

---

## 权限节点

| 权限                             | 默认   | 说明                          |
|--------------------------------|------|-----------------------------|
| `goodstrade.command.toggle`    | true | 切换是否接受交易的状态（防骚扰）            |
| `goodstrade.command`           | true | 使用指令的权限, 无此权限无法使用任何指令       |
| `goodstrade.command.sendtrade` | true | 使用sendTrade指令向其他玩家发起交易请求的权限 |
| `goodstrade.command.accept`    | true | 同意他人交易请求的权限                 |
| `goodstrade.command.trade`     | op   | 强制两人交易的权限                   |
| `goodstrade.command.test`      | op   | 使用虚拟玩家测试交易界面和完整流程         |
| `goodstrade.command.reload`    | op   | 重载插件的权限                     |

---

## 配置说明

配置文件位置：`plugins/GoodsTrade/config.yml`

### 语言切换

首次启动后会生成：

- `plugins/GoodsTrade/lang/zh_cn.yml`
- `plugins/GoodsTrade/lang/en_us.yml`

`Language: system`（包括字段缺失或留空）自动选择语言。依次读取 Java 进程的 `LC_ALL`、`LC_MESSAGES`、`LANG`，使用第一个非空值，支持 `zh_CN.UTF-8` 等格式；没有具体语言、值为 `C`／`POSIX` 或格式无效时，使用 JVM 默认语言。高优先级变量会覆盖低优先级变量。自动模式每次启动或重载都会在后台同时输出中英文设置提示，显示当前语言、配置位置及 `/gt reload` 命令；明确指定语言时不输出这组提示。

语言文件先精确匹配；未找到区域版本时，中文使用 `zh_cn`，英语使用 `en_us`，其它语言只在同语言的区域版本唯一时匹配。仍没有对应翻译时提示并回退到简体中文。明确配置的语言代码仍按文件名精确匹配。

Linux 的进程语言取决于服务、面板或容器的启动环境，服务器所在地区不能决定语言。即使登录终端是中文，启动 Minecraft 的服务也可能继承英语或 `C.UTF-8`。本插件仅读取本地环境，不进行网络定位。固定使用中文最可靠的方式是设置 `Language: zh_cn`；修改进程环境后需要重启 Java 进程，仅重载插件不会改变其已继承的环境。JVM 默认语言只在上述环境未提供具体语言时使用。

也可以指定语言，然后执行 `/gt reload`：

```yaml
Language: system # 自动检测
# Language: zh_cn # 简体中文
# Language: en_us # English (US)
```

插件会自动释放 JAR 内 `lang/` 目录中的所有语言文件，只补充缺少的文件，不覆盖服主已经修改的翻译。因此未来版本加入 `ja_jp.yml` 等翻译时，升级后会自动生成。

直接输入 `/gt` 时显示的命令功能说明来自语言文件中的 `command.help-entry` 和 `command.description`，服主可自行修改措辞。

从旧版本升级时，已有的根目录 `Lang.yml` 会在需要时迁移为 `lang/zh_cn.yml`，原文件不会被删除。

### 生效世界、距离与受伤取消

```yaml
Trade:
  Enabled-Worlds: ["*"]
  Distance:
    Same-World: true
    Start: 4
    Trading: 8
  Safe:
    Close-On-Damage: false
    Damage: false
    Move: false
```

`Enabled-Worlds` 默认 `["*"]`，允许所有世界（包括自定义及后续新增世界）。也可填写具体世界名称列表进行限制，空列表禁用所有世界；已有配置中的具体世界列表仍生效，要开放全部世界请改为 `["*"]`。发起请求和打开交易时都会检查双方，管理员强制交易和测试模式同样遵循限制。玩家进入禁用世界时清除相关请求并取消当前交易、返还物品。

`Distance.Same-World` 默认要求双方在同一世界；`Start` 是发起请求、接受请求及管理员打开交易时的距离上限，默认 4 格；`Trading` 是交易期间上限，默认 8 格。距离按包含高度差的直线距离计算，恰好达到上限仍允许。两项距离独立生效，各设为 `0` 可关闭对应限制；负数或非有限数使用默认值。跨世界交易需同时设置 `Same-World: false`、`Start: 0`、`Trading: 0`，且双方世界都在允许列表内。

交易期间每 5 tick 共享检查一次实际位置，水流、推挤或传送导致超距时会停止倒计时、关闭双方界面、返还物品并提示；结算前再次检查。禁用世界中无法发送请求、接受请求或打开交易，管理员交易和测试模式同样受限。

`Close-On-Damage` 默认关闭。开启后，任一方实际受到伤害时停止倒计时、关闭双方界面并返还物品，包括环境伤害。被其它插件取消或最终伤害为零时不会关闭；`Damage: true` 的交易免伤优先，因此不会触发受伤取消。已有配置缺少新增字段时使用上述默认值；添加字段后执行 `/gt reload` 生效。

### 请求冷却与有效期

```yaml
Trade:
  Request:
    Cooldown: 5
    Expire: 30
```

`Cooldown` 是成功发送请求后，向任意玩家再次发送前的等待秒数，默认 5 秒；设为 0 关闭发送冷却。`Expire` 是对方可以接受这条请求的时间，默认 30 秒。两者独立，例如 A 给 B 发请求，5 秒后可以给 C 发，而 B 的请求仍有效到第 30 秒。同一对玩家已有有效请求时不能重复发送，也不会刷新有效期。冷却范围为 0–86400 秒，有效期为 1–86400 秒，无效值使用默认值。

发送或接受时，只要任一方正在交易就拒绝。开始交易（包括测试模式）会清除双方所有发出和收到的请求，不影响其他玩家彼此之间的请求。因此 C 接受 A 后，B 再点 A 的旧请求会提示没有有效请求，A、C 结束交易后也不会恢复旧请求。请求清除不重置发送冷却；重载会清空请求和冷却。

### 货币与经验等级交易

支持 Vault、PlayerPoints、ExcellentEconomy 和原版经验等级。同一笔交易可以同时包含多种货币，双方也可以支付不同币种。左键金额按钮增加报价，右键减少；左键中央分隔板切换当前调整的币种，已经填写的其它报价会保留。双方必须取消确认后才能切换币种。

中央分隔板列出当前币种和所有非零报价的双方支付金额。修改任何币种的报价都会重置已有确认，倒计时结束后才结算。不同币种不换算、不相互抵扣；每个币种按双方支付差额转账。

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
        Name: "金币"
      levels:
        Enable: true
        Provider: experience
        Name: "级经验"
        Amounts: [1, 5, 10, 30]
      points:
        Enable: true
        Provider: playerpoints
        Name: "点券"
        Amounts: [1, 10, 100, 1000]
      tokens:
        Enable: true
        Provider: excellenteconomy
        Currency: tokens
        Name: "代币"
        Amounts: [1, 10, 100, 1000]
```

- `Trade.Economy.Enable: false` 关闭全部货币与等级交易，同时隐藏金额按钮和动态货币提示，原按钮位置显示背景；无需修改或注释 `View.yml`。单独关闭币种后该币种不再参与交易或切换，所有币种均不可用时仅交易物品。测试模式使用相同的可用币种列表。
- 经济总开关默认关闭。启用 `Trade.Economy.Enable` 后，默认币种条目为 Vault；点券、代币和经验等级需按需开启。旧配置没有 `Currencies` 时，继续使用 Vault 和原有 `Amounts`。
- `Currencies` 下的键是 GoodsTrade 币种标识。`Provider` 支持 `vault`、`playerpoints`、`excellenteconomy`、`experience`；`Name` 是玩家看到的名称，支持 `&` 颜色代码。
- ExcellentEconomy 的 `Currency` 必须填写已有币种 ID。复制条目并更换标识、`Currency` 和 `Name` 即可增加其它币种。
- `experience` 使用原版经验等级，无需前置插件，只接受整数。例如支付 5 级后，30 级变为 25 级，经验条进度不变。
- 每个币种可配置 1–4 档正数 `Amounts`，省略时继承 `Economy.Amounts`。PlayerPoints 仅接受整数，单次支付不超过 `2147483647`；ExcellentEconomy 的整数币种也拒绝小数。
- 缺少插件、币种不存在或 API 不兼容时，只跳过对应条目并在后台说明原因。所有币种均不可用时仍可交易物品。
- 同一底层账户只配置一次。例如 Vault 已连接 ExcellentEconomy 的金币账户时，不要再把该账户作为独立币种加入。
- `Allow-Negative: false` 时报价最低为 `0`。开启后，负数表示要求对方支付同一币种。
- 按钮外观统一在 `View.yml` 配置，切换类型后左右两侧按钮一起更新。默认金币使用金锭、点券使用绿宝石、经验等级使用经验瓶、ExcellentEconomy 代币使用向日葵。
- `currency-defaults.<Provider>` 修改一类经济系统的默认样式；`currency-buttons.<币种ID>.Money` 修改某个币种的样式，`Money-1` 至 `Money-4` 可单独修改其分档按钮。币种 ID 是 `config.yml` 中 `Trade.Economy.Currencies` 下的键，例如 `levels`、`points`。
- 各层均支持 `material`、`name`、`lore`、`custom_model_data`。覆盖顺序为：类型默认 → `button.Money` → `button.Money-1…4` → 币种 `Money` → 币种 `Money-1…4`。后面的配置只覆盖显式填写的字段；原有通用材质仍有效，要使用按类型的默认物品，可移除通用 `material` 或将其设为 `@default@`。
- 名称和 Lore 中 `%amount%` 是包含单位的数量，`%currency%` 是当前类型名称。中央栏的切换提示和双方支付内容追加在自定义 Lore 后。
- `/gt reload` 先取消正在进行的交易并返还物品，再加载新币种配置。

例如在 `View.yml` 中把点券默认物品改成纸，再把 `tokens` 这个币种单独改为钻石：

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

旧 `View.yml` 未包含新增配置节时也会使用按类型的默认物品；需要自定义时添加对应配置节，执行 `/gt reload` 生效。

每次修改金额校验当前币种，确认和结算时校验全部币种。结算失败则返还物品，尝试退回当前扣款，并按反序撤回已完成的其它币种转账。如果经济插件拒绝退款，双方会收到联系管理员的提示，后台记录币种、双方 UUID 和金额。跨插件操作不是数据库原子事务，服务器进程中断或第三方 API 扣款后抛异常仍需依据经济插件流水人工核对。

适配基于 [PlayerPoints UUID API](https://github.com/Rosewood-Development/PlayerPoints/blob/master/src/main/java/org/black_ixx/playerpoints/PlayerPointsAPI.java) 和 [ExcellentEconomy 同步 API](https://github.com/nulli0n/ExcellentEconomy/blob/master/src/main/java/su/nightexpress/excellenteconomy/api/ExcellentEconomyAPI.java)。ExcellentEconomy 需要提供 `getAPI()`、按币种 ID 的 `getBalance/withdraw/deposit` 和币种限制接口的版本；旧 CoinsEngine API 不在此适配范围。GoodsTrade 保留 Java 8 编译目标，经济插件自身的服务端和 Java 要求仍须满足。

本地测试覆盖多币种结算、失败回滚和 API 契约替身，不等于实服联调。部署前用两名玩家验证混合报价、改价撤销确认、倒计时内余额不足、关闭界面、重载取消及最终两端余额。

### 管理员测试模式

在游戏内输入 `/gt test [测试名称]` 可打开测试模式，需要 `goodstrade.command.test` 权限。无需另一名玩家在线，可以检查物品栏、支付按钮、取消确认和倒计时。

测试模式由一名管理员操作双方物品栏和确认按钮。货币与等级保持不变，测试结束、关闭界面或重载时返还所有放入的物品。该命令需要在游戏内执行。

---

## 使用方法

### 基础交易流程

1. **发起交易**：
    - 方式一：输入命令 `/gt sendtrade [玩家名]`
    - 方式二：潜行状态下右键点击玩家（需配置开启）

2. **接受交易**：
    - 点击聊天栏中的 `[点击接受]` 链接
    - 或输入命令 `/gt accept`

3. **放置物品**：将想要交易的物品放入交易界面左侧（发起者）或右侧（接收者）

4. **确认交易**：点击按钮变绿后确认，双方都确认后进入倒计时

5. **完成交易**：倒计时结束后，货币与经验等级完成支付且物品自动交换

### 交易提示

- ✅ 确认后将锁定物品，无法再修改
- ⏰ 双方都确认后开始 5 秒倒计时(配置文件修改)
- ❌ 倒计时期间可取消，回到初始状态
- 🎒 交易取消或关闭界面时，物品自动返还
- 💵 任一方修改支付数量后，已有的确认会被重置，需要双方重新检查并确认

---

## 待办功能

- [x] 物品黑名单系统
- [x] 权限模块完善
- [x] 自定义界面材质、描述等
- [x] 支持 Vault、PlayerPoints、ExcellentEconomy 多货币交易
- [x] 支持原版经验等级交易
- [ ] 交易流水（已列入计划）：记录时间、交易 ID、双方 UUID／名称、物品快照、各币种金额、成功／取消／结算失败及退款异常；提供管理员查询、保留期限和清理配置。
- [ ] 自定义交易要求，服务器可设置
- [x] 独立配置玩家全局请求冷却和请求有效期
- [ ] 可疑交易警告系统
- [x] 同世界限制、发起／接受距离及交易期间超距取消

---

## 已知问题

- 玩家名字过长时标题显示可能有点奇怪

---

## 支持与反馈

如遇到问题或有功能建议，请通过以下方式联系：

- 💬 QQ 群：620224543
- 📝 Issues: [提交问题](https://github.com/polang233/GoodsTrade/issues)

---

**如果觉得好用，请给个 ⭐ Star 支持一下！**

## 使用统计

[![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)](https://bstats.org/plugin/bukkit/GoodsTrade/30110)
