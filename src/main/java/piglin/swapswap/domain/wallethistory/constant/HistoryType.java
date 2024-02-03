package piglin.swapswap.domain.wallethistory.constant;

public enum HistoryType {

    NORMAL_DEPOSIT(HistoryTypeName.NORMAL_DEPOSIT),
    NORMAL_WITHDRAW(HistoryTypeName.NORMAL_WITHDRAW),
    DEAL_DEPOSIT(HistoryTypeName.DEAL_DEPOSIT),
    TEMPORARY_WITHDRAW(HistoryTypeName.TEMPORARY_WITHDRAW),
    CANCEL_WITHDRAW(HistoryTypeName.CANCEL_WITHDRAW);
    
    private final String type;

    HistoryType(String type) {
        this.type = type;
    }

    public static class HistoryTypeName {
        public static String NORMAL_DEPOSIT = "일반 입금";
        public static String NORMAL_WITHDRAW = "일반 출금";
        public static String DEAL_DEPOSIT = "거래 입금";
        public static String TEMPORARY_WITHDRAW = "임시 출금";
        public static String CANCEL_WITHDRAW = "출금 취소";
    }
}
