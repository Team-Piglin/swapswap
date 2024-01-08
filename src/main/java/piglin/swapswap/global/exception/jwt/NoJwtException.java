package piglin.swapswap.global.exception.jwt;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class NoJwtException extends BusinessException {

    public NoJwtException() {
        super(ErrorCode.NO_JWT_EXCEPTION);
    }
}
