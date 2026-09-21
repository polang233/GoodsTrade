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

- **安全保护**：可设置交易期间免疫伤害、限制移动；背包放不下的返还物品会掉落在脚下
- **可视化界面**：箱子界面操作，也可蹲下右键发送请求；对方可再蹲下右键快捷回应
- **确认机制**：双方确认后开始倒计时，期间可以取消并重新调整
- **物品锁定**：确认后双方无法更改交易物品，防止受骗
- **多货币交易**：支持 Vault、PlayerPoints、ExcellentEconomy 和经验等级，余额不足时无法报价或结算，改价会重置已有确认
- **可配置**：等待时间、触发方式、距离和生效世界等

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

首次启动会生成 `plugins/GoodsTrade/lang/zh_cn.yml` 和 `en_us.yml`。JAR 内尚不存在的语言文件会补到磁盘；已有文件里缺少的键会从默认文件写入，并在后台列出补上的项，已有翻译不会改动。旧版根目录 `Lang.yml` 会在需要时复制为 `lang/zh_cn.yml`。

`Language: system`（缺省或留空）按进程的 `LC_ALL`、`LC_MESSAGES`、`LANG` 自动选择，没有具体语言时再用 JVM 默认语言。Linux 上 Minecraft 进程的语言取决于启动环境，不一定等于终端；要固定中文请设 `Language: zh_cn`，改环境变量后需重启 Java。自动模式会在后台输出中英文提示。

```yaml
Language: system # 自动检测
# Language: zh_cn # 简体中文
# Language: en_us # English (US)
```

`/gt` 帮助文案来自 `command.help-entry` 和 `command.description`。`%label%` 会替换成玩家实际输入的主命令（`gt` 或 `goodstrade`）。

### 生效世界、距离与受伤取消

```yaml
Trade:
  Invert-Enabled-Worlds: false
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

`Enabled-Worlds` 默认 `["*"]` 允许全部世界（含后续新增世界）；也可填世界名列表，空列表则全部禁用。`Invert-Enabled-Worlds` 默认 `false` 为白名单（列出的世界可以交易）；设为 `true` 后反选为黑名单（列出的世界不能交易，未列出的可以）。反选时 `["*"]` 禁止所有世界，空列表不禁止任何世界。发起、接受、管理员交易和测试模式都会检查双方。进入禁用世界会清除相关请求，并取消当前交易、返还物品。

`Same-World` 默认要求同世界。`Start`（默认 4）限制发起、接受和打开界面，`Trading`（默认 8）限制交易中。距离含高度，恰好达到上限仍允许；`0` 关闭该项，无效值用默认。跨世界需 `Same-World: false` 且两项距离均为 `0`。交易中每 5 tick 检查一次实际位置，超距则取消并返还物品；结算前再检查一次。

`Close-On-Damage` 默认关闭。开启后，未取消且最终伤害大于 0 时取消交易；`Damage: true` 的免伤优先。已有配置缺少字段时使用上述默认值。

### 请求冷却与有效期

```yaml
Trade:
  Request:
    Cooldown: 5
    Expire: 30
```

`Cooldown` 是成功发送后、向任意玩家再次发送前的等待秒数，默认 5，`0` 关闭。`Expire` 是对方可接受该请求的时限，默认 30 秒。两者独立：例如 5 秒后可向其他人再发，前一条请求仍可被接受至到期。同一对玩家已有有效请求时不能重发。冷却 0–86400 秒，有效期 1–86400 秒，无效值用默认。

任一方正在交易时不能发送或接受。开始交易（含测试）会清除双方相关请求，不影响其他人；重载清空请求和冷却。

### 快捷触发

```yaml
Trade:
  Triggers:
    Shift-Right-Click: true
    Shift-Right-Click-Accept: true
```

`Shift-Right-Click` 蹲下右键玩家发起请求。`Shift-Right-Click-Accept` 收到请求后，蹲下右键发起者即可接受。两个开关独立。

### 货币与经验等级交易

支持 Vault、PlayerPoints、ExcellentEconomy 和原版经验等级，同一笔交易可含多种货币。左键金额按钮增加、右键减少；左键中央分隔板切换币种（需先取消确认）。各币种按双方支付差额分别结算，不换算、不互抵。改价会重置确认。

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

- `Economy.Enable` 总开关，默认关闭；关闭后隐藏金额按钮，只交易物品。测试模式使用同一套可用币种。
- `Currencies` 的键是币种 ID。`Provider` 支持 `vault`、`playerpoints`、`excellenteconomy`、`experience`；`Name` 是玩家看到的名称，可用 `&` 颜色。旧配置没有 `Currencies` 时继续用 Vault 和原有 `Amounts`。
- ExcellentEconomy 的 `Currency` 填已有币种 ID。`experience` 用原版等级、只接受整数（例如支付 5 级后 30 级变为 25 级，经验条进度不变）。
- 每个币种可配 1–4 档正数 `Amounts`，省略则继承 `Economy.Amounts`。PlayerPoints 和整数币种不接受小数。
- 缺少插件、币种不存在或 API 不兼容时跳过该项；所有币种都不可用时仍可交易物品。同一底层账户不要配两次。
- `Allow-Negative: false` 时报价最低为 `0`；开启后负数表示要求对方支付同一币种。
- 按钮外观在 `View.yml`：`currency-defaults.<Provider>` 改一类经济系统的默认样式，`currency-buttons.<币种ID>` 改单个币种。覆盖顺序为类型默认 → `button.Money` → 分档按钮 → 币种条目；后面只覆盖显式填写的字段。
- `/gt reload` 会先取消进行中的交易并返还物品，再加载新币种配置。

例如把点券默认物品改成纸，再把 `tokens` 单独改为钻石：

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

名称和 Lore 中 `%amount%` 是带单位的数量，`%currency%` 是当前类型名称。改金额时校验当前币种，确认和结算时校验全部币种。结算失败返还物品并尝试退款；经济插件拒绝退款时双方会收到提示，后台记录币种、UUID 和金额。这不是跨插件数据库事务，异常时需对照经济插件流水核对。

适配 [PlayerPoints UUID API](https://github.com/Rosewood-Development/PlayerPoints/blob/master/src/main/java/org/black_ixx/playerpoints/PlayerPointsAPI.java) 和 [ExcellentEconomy 同步 API](https://github.com/nulli0n/ExcellentEconomy/blob/master/src/main/java/su/nightexpress/excellenteconomy/api/ExcellentEconomyAPI.java)；不支持旧 CoinsEngine。插件保持 Java 8 编译目标。上线前建议用两名玩家走一遍混合报价、改价、余额不足和关闭界面。

### 管理员测试模式

游戏内 `/gt test [测试名称]` 可打开测试模式，需要 `goodstrade.command.test`。无需另一名玩家在线，由一名管理员操作双方物品栏和确认按钮。货币与等级不扣除，结束、关闭或重载时返还放入的物品。

---

## 使用方法

### 基础交易流程

1. **发起交易**：
    - 命令 `/gt sendtrade [玩家名]`
    - 或潜行右键玩家（需开启 `Trade.Triggers.Shift-Right-Click`）

2. **接受交易**：
    - 点击聊天栏中的 `[点击接受]`
    - 或潜行右键发起者（需开启 `Trade.Triggers.Shift-Right-Click-Accept`）
    - 或命令 `/gt accept`

3. **放置物品**：将要交易的物品放入界面左侧（发起者）或右侧（接收者）

4. **确认交易**：点击按钮确认，双方都确认后进入倒计时

5. **完成交易**：倒计时结束后完成支付并交换物品

### 交易提示

- 确认后物品锁定，无法再修改
- 双方都确认后开始倒计时（默认 5 秒，可在配置中修改）
- 倒计时期间可取消，回到未确认状态
- 取消交易或关闭界面时，物品自动返还
- 任一方修改支付数量后，已有确认会被重置

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

- QQ 群：620224543
- Issues: [提交问题](https://github.com/polang233/GoodsTrade/issues)

---

**如果觉得好用，请给个 ⭐ Star 支持一下！**

## 使用统计

[![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)](https://bstats.org/plugin/bukkit/GoodsTrade/30110)
