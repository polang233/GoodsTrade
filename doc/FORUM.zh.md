# GoodsTrade

**通过箱子界面交换物品、货币和经验等级，双方确认后倒计时结算。支持 Bukkit、Spigot 和 Paper。**


![Version](https://img.shields.io/github/v/release/polang233/GoodsTrade?label=Version&color=2ea44f)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12--26.2-62b47a)
![Servers](https://img.shields.io/badge/servers-Bukkit%20%7C%20Spigot%20%7C%20Paper-f4a940)
![Java](https://img.shields.io/badge/Java-8%2B-e76f00)

## 交易界面

![双方确认与交易倒计时](https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/QQ20260315-190201-HD.gif)
![双方物品报价界面](https://raw.githubusercontent.com/polang233/GoodsTrade/master/img/1773587841043.webp)


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

## 货币与菜单

在 `Trade.Economy.Currencies` 中配置 Vault、PlayerPoints、ExcellentEconomy 或经验等级。每个币种可设置名称和 1–4 档金额按钮；默认只启用 Vault，其它提供方需要自行开启。

左键金额按钮增加报价，右键减少。左键中央分隔板切换币种，已有报价保留；确认后需要先取消确认再切换。不同币种分别结算，不进行汇率换算。

`View.yml` 可设置菜单背景、确认按钮、金额按钮，以及各币种的默认外观或单独外观。完整配置见[使用说明](https://github.com/polang233/GoodsTrade/blob/master/README.md#配置说明)。

## 语言与使用

默认跟随服务器系统语言，缺少对应翻译时回退简体中文。可以设置 `Language: zh_cn` 或 `Language: en_us`，修改后执行 `/gt reload`。

1. 使用 `/gt sendtrade 玩家名` 或蹲下右键发送请求。
2. 对方通过聊天提示或 `/gt accept` 接受。
3. 双方放入物品、设置需要支付的金额或等级。
4. 双方确认，倒计时结束后完成交易。

取消交易或关闭界面会返还物品；背包放不下的物品掉落在玩家脚下。管理员可使用 `/gt test` 单人检查交易流程。

## 支持与反馈

[GitHub Issues](https://github.com/polang233/GoodsTrade/issues) · QQ 群：620224543

如果插件对你有帮助，欢迎给[项目](https://github.com/polang233/GoodsTrade)点个 ⭐ Star。

## 使用统计

[![GoodsTrade bStats](https://bstats.org/signatures/bukkit/GoodsTrade.svg)](https://bstats.org/plugin/bukkit/GoodsTrade/30110)
