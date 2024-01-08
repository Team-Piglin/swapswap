package piglin.swapswap.global.exception.user;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.NOT_FOUND_USER_EXCEPTION);
    }
}
