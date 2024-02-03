package piglin.swapswap.global.exception.common;

public class UnauthorizedModifyException extends BusinessException{

    public UnauthorizedModifyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
