package piglin.swapswap.global.exception.post;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class AlreadyDealingPostException extends BusinessException {

    public AlreadyDealingPostException() {
        super(ErrorCode.ALERADY_DELING_POST_EXCEPTION);
    }
}
