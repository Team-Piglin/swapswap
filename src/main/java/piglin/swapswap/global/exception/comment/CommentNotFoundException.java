package piglin.swapswap.global.exception.comment;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class CommentNotFoundException extends BusinessException {

    public CommentNotFoundException() {
        super(ErrorCode.NOT_FOUND_COMMENT_EXCEPTION);
    }
}
