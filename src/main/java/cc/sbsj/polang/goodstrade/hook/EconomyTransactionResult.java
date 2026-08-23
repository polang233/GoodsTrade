package cc.sbsj.polang.goodstrade.hook;

public final class EconomyTransactionResult {
    private final boolean success;
    private final String errorMessage;

    private EconomyTransactionResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public static EconomyTransactionResult success() {
        return new EconomyTransactionResult(true, "");
    }

    public static EconomyTransactionResult failure(String errorMessage) {
        return new EconomyTransactionResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
