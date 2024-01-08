package piglin.swapswap.global.exception.user;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class PasswordMismatchedException extends BusinessException {

    public PasswordMismatchedException() {
        super(ErrorCode.MISMATCHED_PASSWORD_EXCEPTION);
    }
}
