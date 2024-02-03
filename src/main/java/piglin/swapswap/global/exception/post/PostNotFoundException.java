package piglin.swapswap.global.exception.post;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(ErrorCode.NOT_FOUND_POST_EXCEPTION);
    }
}
