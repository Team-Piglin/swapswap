package piglin.swapswap.global.exception.user;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class EmailAlreadyExistException extends BusinessException {

    public EmailAlreadyExistException() {
        super(ErrorCode.ALREADY_EXIST_EMAIL_EXCEPTION);
    }
}
