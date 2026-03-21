package cc.sbsj.polang.goodstrade.task;

import cc.sbsj.polang.goodstrade.trade.TradeManager;

public class RunTask implements Runnable {
    public void run() {
        TradeManager.cleanupExpiredRequests();
    }
}
