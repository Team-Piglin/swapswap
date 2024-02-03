package piglin.swapswap.global.exception.deal;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class DealNotFoundException extends BusinessException {

    public DealNotFoundException(ErrorCode errorCode) {super(errorCode);}
}
