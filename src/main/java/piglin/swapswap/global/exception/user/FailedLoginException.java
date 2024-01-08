package piglin.swapswap.global.exception.user;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class FailedLoginException extends BusinessException {

    public FailedLoginException(Throwable cause) {
        super(ErrorCode.FAILED_LOGIN_EXCEPTION, cause);
    }
}
