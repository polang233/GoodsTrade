<p align="center">
  <img src="img/logo.png" alt="GoodsTrade Logo" width="220">
</p>

# GoodsTrade 玩家交易

> 通过箱子界面交换物品、货币和经验等级，支持 Bukkit、Spigot 和 Paper。

> English-speaking server owners: read the full [English documentation](doc/README_EN.md).

![Version](https://img.shields.io/github/v/release/polang233/GoodsTrade?label=Lite&color=2ea44f)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.2-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)
[![Powered by OrcaRouter](https://img.shields.io/badge/Powered_by-OrcaRouter-2563eb)](https://www.orcarouter.ai/ref/ref_1095590f527d3ae7ae2f)

![示例](/img/QQ20260315-190201-HD.gif "交易界面演示")
![示例2](/img/1773587841043.webp "交易界面演示2")

---

## 📖 简介

双方在同一个箱子界面中放入物品、设置支付数量，确认后开始倒计时。交易结束前可以取消，关闭界面会返还物品。

### ✨ 特性

- 🛡️ **安全保护**：可设置交易期间免疫伤害、限制移动；背包放不下的返还物品会掉落在脚下
- 🎯 **可视化界面**：通过箱子界面操作，也可蹲下右键玩家发送请求
- ⏱️ **确认机制**：双方确认后开始倒计时，期间可以取消并重新调整
- 🔒 **物品锁定**：确认后双方无法更改交易物品，防止受骗
- 💰 **多货币交易**：支持 Vault、PlayerPoints、ExcellentEconomy 和经验等级，余额不足时无法报价或结算，改价会重置已有确认
- ⚙️ **可配置**：支持自定义等待时间、触发方式等

### 前置要求（可选）

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- 货币交易可选接入 [Vault](https://www.spigotmc.org/resources/vault.34315/) 经济服务、PlayerPoints 或 ExcellentEconomy；未安装时仍可正常交易物品

## 🚀 命令

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

## 🔐 权限节点

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

## ⚙️ 配置说明

配置文件位置：`plugins/GoodsTrade/config.yml`

### 🌐 语言切换

首次启动后会生成：

- `plugins/GoodsTrade/lang/zh_cn.yml`
- `plugins/GoodsTrade/lang/en_us.yml`

默认使用服务器 JVM/操作系统语言。`config.yml` 中没有 `Language` 的旧配置也会使用系统语言；若没有对应翻译，控制台会提示并回退到简体中文。

也可以指定语言，然后执行 `/gt reload`：

```yaml
Language: system # 自动检测
# Language: zh_cn # 简体中文
# Language: en_us # English (US)
```

插件会自动释放 JAR 内 `lang/` 目录中的所有语言文件，只补充缺少的文件，不覆盖服主已经修改的翻译。因此未来版本加入 `ja_jp.yml` 等翻译时，升级后会自动生成。

直接输入 `/gt` 时显示的命令功能说明来自语言文件中的 `command.help-entry` 和 `command.description`，服主可自行修改措辞。

从旧版本升级时，已有的根目录 `Lang.yml` 会在需要时迁移为 `lang/zh_cn.yml`，原文件不会被删除。

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

- 默认配置仅启用 Vault，点券、代币和经验等级默认关闭。按已安装的插件开启对应条目。旧配置没有 `Currencies` 时，继续使用 Vault 和原有 `Amounts`。
- `Currencies` 下的键是 GoodsTrade 币种标识。`Provider` 支持 `vault`、`playerpoints`、`excellenteconomy`、`experience`；`Name` 是玩家看到的名称，支持 `&` 颜色代码。
- ExcellentEconomy 的 `Currency` 必须填写已有币种 ID。复制条目并更换标识、`Currency` 和 `Name` 即可增加其它币种。
- `experience` 使用原版经验等级，无需前置插件，只接受整数。例如支付 5 级后，30 级变为 25 级，经验条进度不变。
- 每个币种可配置 1–4 档正数 `Amounts`，省略时继承 `Economy.Amounts`。PlayerPoints 仅接受整数，单次支付不超过 `2147483647`；ExcellentEconomy 的整数币种也拒绝小数。
- 缺少插件、币种不存在或 API 不兼容时，只跳过对应条目并在后台说明原因。所有币种均不可用时仍可交易物品。
- 同一底层账户只配置一次。例如 Vault 已连接 ExcellentEconomy 的金币账户时，不要再把该账户作为独立币种加入。
- `Allow-Negative: false` 时报价最低为 `0`。开启后，负数表示要求对方支付同一币种。
- 金额按钮样式由 `View.yml` 的 `Money`、`Money-1` 至 `Money-4` 控制。只改材质会保留默认名称和 Lore，分档按钮省略的字段继承 `Money`。中央栏的当前类型、切换提示和双方支付内容追加在自定义 Lore 后。`%amount%` 是包含币种名称的金额，`%currency%` 是当前币种名称。
- `/gt reload` 先取消正在进行的交易并返还物品，再加载新币种配置。

每次修改金额校验当前币种，确认和结算时校验全部币种。结算失败则返还物品，尝试退回当前扣款，并按反序撤回已完成的其它币种转账。如果经济插件拒绝退款，双方会收到联系管理员的提示，后台记录币种、双方 UUID 和金额。跨插件操作不是数据库原子事务，服务器进程中断或第三方 API 扣款后抛异常仍需依据经济插件流水人工核对。

适配基于 [PlayerPoints UUID API](https://github.com/Rosewood-Development/PlayerPoints/blob/master/src/main/java/org/black_ixx/playerpoints/PlayerPointsAPI.java) 和 [ExcellentEconomy 同步 API](https://github.com/nulli0n/ExcellentEconomy/blob/master/src/main/java/su/nightexpress/excellenteconomy/api/ExcellentEconomyAPI.java)。ExcellentEconomy 需要提供 `getAPI()`、按币种 ID 的 `getBalance/withdraw/deposit` 和币种限制接口的版本；旧 CoinsEngine API 不在此适配范围。GoodsTrade 保留 Java 8 编译目标，经济插件自身的服务端和 Java 要求仍须满足。

本地测试覆盖多币种结算、失败回滚和 API 契约替身，不等于实服联调。部署前用两名玩家验证混合报价、改价撤销确认、倒计时内余额不足、关闭界面、重载取消及最终两端余额。

### 🧪 管理员测试模式

在游戏内输入 `/gt test [测试名称]` 可打开测试模式，需要 `goodstrade.command.test` 权限。无需另一名玩家在线，可以检查物品栏、支付按钮、取消确认和倒计时。

测试模式由一名管理员操作双方物品栏和确认按钮。货币与等级保持不变，测试结束、关闭界面或重载时返还所有放入的物品。该命令需要在游戏内执行。

---

## 🎮 使用方法

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

### 💡 交易提示

- ✅ 确认后将锁定物品，无法再修改
- ⏰ 双方都确认后开始 5 秒倒计时(配置文件修改)
- ❌ 倒计时期间可取消，回到初始状态
- 🎒 交易取消或关闭界面时，物品自动返还
- 💵 任一方修改支付数量后，已有的确认会被重置，需要双方重新检查并确认

---

## 📋 待办功能

- [x] 物品黑名单系统
- [x] 权限模块完善
- [x] 自定义界面材质、描述等
- [x] 支持 Vault、PlayerPoints、ExcellentEconomy 多货币交易
- [x] 支持原版经验等级交易
- [ ] 交易历史记录
- [ ] 自定义交易要求，服务器可设置
- [ ] 交易冷却时间设置
- [ ] 可疑交易警告系统
- [ ] 玩家双方距离过远取消交易

---

## 🐛 已知问题

- 玩家名字过长时标题显示可能有点奇怪

---

## 📞 支持与反馈

如遇到问题或有功能建议，请通过以下方式联系：

- 💬 QQ 群：620224543
- 📝 Issues: [提交问题](https://github.com/polang233/GoodsTrade/issues)

---

![统计](https://bstats.org/signatures/bukkit/GoodsTrade.svg "使用统计")
---


<div style="text-align: center">

**如果觉得好用，请给个 ⭐ Star 支持一下！**

</div>
