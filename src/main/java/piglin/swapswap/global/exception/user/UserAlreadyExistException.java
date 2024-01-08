package piglin.swapswap.global.exception.user;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class UserAlreadyExistException extends BusinessException {

    public UserAlreadyExistException() {
        super(ErrorCode.ALREADY_EXIST_USER_NAME_EXCEPTION);
    }
}
