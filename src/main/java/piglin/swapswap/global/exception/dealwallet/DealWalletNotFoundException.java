package piglin.swapswap.global.exception.dealwallet;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class DealWalletNotFoundException extends BusinessException {

    public DealWalletNotFoundException() {
        super(ErrorCode.NOT_FOUND_DEAL_WALLET_EXCEPTION);
    }
}
