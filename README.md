<p align="center">
  <img src="img/logo.png" alt="GoodsTrade Logo" width="220">
</p>

# GoodsTrade - 简单安全的玩家物品交易插件

>  Minecraft 服务器的轻量物品交易插件，提供安全、便捷的玩家间交易功能。

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

GoodsTrade 是一个简单易用的玩家交易插件，支持可视化 GUI 界面操作。玩家可以安全地交换物品，避免交易诈骗。

### ✨ 特性

- 🛡️ **安全保护**：支持交易期间可设置免疫伤害和行动限制，物品返还均进行处理防止背包容量不足
- 🎯 **可视化界面**：直观的 GUI 操作，无需复杂命令，可玩家蹲下右键快捷发起
- ⏱️ **确认机制**：双方确认后进行倒计时，期间若发现物品不对可取消，确保交易安全
- 🔒 **物品锁定**：确认后双方无法更改交易物品，防止受骗
- 💰 **Vault 金币交易**：双方可在界面中调整支付金额，余额不足时无法报价或结算，改价会重置已有确认
- ⚙️ **可配置**：支持自定义等待时间、触发方式等

### 前置要求（可选）

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- 金币交易需要 [Vault](https://www.spigotmc.org/resources/vault.34315/) 和一个支持 Vault 的经济插件；未安装时仍可正常交易物品

## 🚀 命令

| 命令                        | 权限                             | 说明          |
|---------------------------|--------------------------------|-------------|
| `/gt sendtrade [玩家名]`     | `goodstrade.command.sendtrade` | 向指定玩家发起交易   |
| `/gt toggle (true/false)` | `goodstrade.command.toggle`    | 更改是否接受交易状态  |
| `/gt trade [发起者] [接收者]`   | `goodstrade.command.trade`     | 让设定两个玩家进行交易 |
| `/gt accept`              | `goodstrade.command.accept`    | 接受当前交易请求    |
| `/gt accept [玩家名]`        | `goodstrade.command.accept`    | 接受指定玩家的交易请求 |
| `/gt test [虚拟玩家名]`       | `goodstrade.command.test`      | 与虚拟玩家进行沙盒测试交易 |
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
| `goodstrade.command.test`      | op   | 使用虚拟玩家沙盒测试交易界面和完整流程         |
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

### 💰 Vault 金币交易

安装 Vault 和经济插件后，玩家可通过交易界面两侧的金币按钮调整报价。左键增加、右键减少；中央分隔板会始终显示双方最终需要支付的金额。任一方改价都会撤销对方已有的确认并发送提示，双方重新确认且倒计时结束后才会结算。

```yaml
Trade:
  Economy:
    Enable: true
    Allow-Negative: false
    Amounts:
      - 1000
      - 10000
```

- `Amounts` 支持 1–4 档正数金额，每档会在双方各生成一个按钮；按钮样式可在 `View.yml` 的 `Money`、`Money-1` 至 `Money-4` 中覆盖，`%amount%` 表示该档金额。
- `Allow-Negative: false` 时每人的报价最低为 `0`。
- `Allow-Negative: true` 时，报价可减为负数，负数表示要求对方支付。例如双方从 `0` 开始，玩家 1 右键减少 `10000` 后，中央信息会显示玩家 2 需支付 `10000`。
- 每次点击、玩家确认及最终结算前都会再次验证双方余额；任何一方余额不足或 Vault 结算失败时，金币不会继续结算，物品会返还。

### 🧪 管理员沙盒测试

拥有 `goodstrade.command.test` 权限的玩家可执行 `/gt test [虚拟玩家名]`。该命令会创建一个不存在的虚拟交易对象，管理员可以操作左右两侧物品格、双方金额按钮和双方确认按钮，用于检查界面、负数金额、确认重置及倒计时流程。

沙盒模式不会调用 Vault，也不会实际交换物品；完成、关闭或重载时，放入左右两侧的物品都会返还给管理员。控制台不能执行此命令。

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

5. **完成交易**：倒计时结束后，金币完成结算且物品自动交换

### 💡 交易提示

- ✅ 确认后将锁定物品，无法再修改
- ⏰ 双方都确认后开始 5 秒倒计时(配置文件修改)
- ❌ 倒计时期间可取消，回到初始状态
- 🎒 交易取消或关闭界面时，物品自动返还
- 💵 任一方修改金币报价后，已有的确认会被重置，需要双方重新检查并确认

---

## 📋 待办功能

- [x] 物品黑名单系统
- [x] 权限模块完善
- [x] 自定义界面材质、描述等
- [x] 支持 Vault 金币交易
- [ ] 支持等级等其他交易内容
- [ ] 交易历史记录
- [ ] 自定义交易要求，服务器可设置
- [ ] 交易冷却时间设置
- [ ] 可疑交易警告系统
- [ ] 玩家双方距离过远取消交易（似乎没必要）

---

## 🐛 已知问题

- 玩家名字过长时标题显示可能有点奇怪

---

## 📞 支持与反馈

如遇到问题或有功能建议，请通过以下方式联系：

- 💬 QQ 群：620224543
- 📝 Issues: [就在这里](https://github.com/polang233/GoodsTrade/issues)
- 🔧 有问题直接联系我就行

---

![统计](https://bstats.org/signatures/bukkit/GoodsTrade.svg "使用统计")
---


<div style="text-align: center">

**如果觉得好用，请给个 ⭐ Star 支持一下！**

</div>
