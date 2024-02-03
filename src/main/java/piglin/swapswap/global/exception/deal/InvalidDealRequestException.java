package piglin.swapswap.global.exception.deal;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class InvalidDealRequestException extends BusinessException {

    public InvalidDealRequestException(ErrorCode errorCode) {super(errorCode);}
}
