## GoodsTrade Lite 1.1.9

### 主要改动

- 支持 Vault、PlayerPoints、ExcellentEconomy 和原版经验等级，同一笔交易可同时加入多种货币。经验按整数等级支付，保留经验条进度。
- 左键中央栏切换交易类型，保留已设置的数量，并显示双方需要支付的内容。金币、点券、经验等级和代币分别使用金锭、绿宝石、经验瓶和向日葵作为默认按钮。
- 修改物品或支付数量后撤销已有确认；支付失败时返还物品，并尝试退回已完成的款项。
- 修复交易请求来源与过期校验、管理员直接交易、代发权限和负数倒计时问题。
- 修复自定义按钮只更换材质时丢失名称与 Lore 的问题。分档按钮继承通用样式，自定义中央栏保留切换提示和支付明细。

### 配置变更

- `config.yml` 新增 `Trade.Economy.Currencies`，每种类型可设置 `Enable`、`Provider`、`Name` 和 `Amounts`。旧配置继续使用 Vault，点券、代币和经验等级需按需开启。
- 经验等级使用 `Provider: experience`；ExcellentEconomy 使用 `Provider: excellenteconomy`，并填写已有币种的 `Currency` ID。同一底层账户只配置一次。
- `View.yml` 新增 `currency-defaults` 和 `currency-buttons`，可按经济类型或币种 ID 设置材质、名称、Lore、模型数据及分档外观。原有 `button.Money`、`Money-1` 至 `Money-4` 继续生效，具体币种配置优先。
- 名称和 Lore 支持 `%amount%` 和 `%currency%`，只改材质会继承其余样式。若通用 `Money` 已写死材质，移除该字段或设为 `@default@` 后即可使用按类型的默认物品。
- 新增切换提示位于语言文件的 `trade-view.currency-selected` 和 `trade-view.currency-switch`。旧语言文件缺少这些键时自动使用内置内容；自定义 Lore 中写死的“金币”等文字需要自行调整。

[默认配置](https://github.com/polang233/GoodsTrade/blob/1.1.9/src/main/resources/config.yml) · [界面配置](https://github.com/polang233/GoodsTrade/blob/1.1.9/src/main/resources/View.yml) · [使用说明](https://github.com/polang233/GoodsTrade/blob/1.1.9/README.md)
