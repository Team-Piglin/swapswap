package piglin.swapswap.global.exception.coupon;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class DuplicateCouponTypeException extends BusinessException {

    public DuplicateCouponTypeException() {
        super(ErrorCode.DUPLICATE_COUPON_TYPE_EXCEPTION);
    }
}
