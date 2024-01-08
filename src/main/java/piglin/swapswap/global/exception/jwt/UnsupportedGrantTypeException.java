package piglin.swapswap.global.exception.jwt;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class UnsupportedGrantTypeException extends BusinessException {

    public UnsupportedGrantTypeException() {
        super(ErrorCode.NOT_SUPPORTED_GRANT_TYPE_EXCEPTION);
    }
}
