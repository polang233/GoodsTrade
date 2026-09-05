## GoodsTrade Lite 1.1.9

### 新增

- 支持 Vault、PlayerPoints 和 ExcellentEconomy。同一笔交易可加入多种货币，每种货币可单独配置名称和金额按钮。
- 支持原版经验等级交易，无需前置插件。按整数等级支付，保留经验条进度。
- 左键中央栏切换交易类型，已设置的数量会保留；中央栏显示双方需要支付的内容。

### 修复与调整

- 修改交易物品或支付数量后，撤销已有确认，提醒对方重新检查。
- 支付失败时返还物品，并尝试退回已完成的款项；无法自动退款时提示管理员核对。
- 接受交易时校验请求发送者与有效期，修复控制台执行接受命令报错的问题。
- 管理员可直接为两名玩家打开交易；代发请求需要管理员权限。
- 修复负数倒计时无法正常结束的问题。
- 统一“测试模式”提示，调整界面、命令和文档说明，并整理经济适配器的包结构。

### 升级说明

1. 备份 `plugins/GoodsTrade/`，停服后替换 JAR，再启动服务器。
2. 旧配置继续使用 Vault。要增加货币或经验等级，请参考[默认配置](https://github.com/polang233/GoodsTrade/blob/1.1.9/src/main/resources/config.yml)，补充 `Trade.Economy.Currencies`。新配置中的点券、代币和经验等级默认关闭，按需开启。
3. 经验等级使用 `Provider: experience`；ExcellentEconomy 需要填写已有币种的 `Currency` ID。同一底层账户只配置一次。
4. 已有配置、`View.yml` 和语言文件不会覆盖。需要新版提示时，备份后对照[语言文件](https://github.com/polang233/GoodsTrade/tree/1.1.9/src/main/resources/lang)更新。
5. 升级后可用 `/gt test` 检查界面，再用两名玩家核对实际支付与取消交易。ExcellentEconomy 需要提供新版同步 API，具体接口要求见[使用说明](https://github.com/polang233/GoodsTrade/blob/1.1.9/README.md)。

已通过 26 项本地测试和构建。保持 Java 8 编译目标；各经济插件自身的运行要求仍须满足。尚未完成实服联调，跨插件支付也不提供服务器进程中断后的原子回滚。
