package piglin.swapswap.global.exception.membercoupon;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class MemberCouponNotFoundException extends BusinessException {
    public MemberCouponNotFoundException() {
        super(ErrorCode.NOT_FOUND_MEMBER_COUPON_EXCEPTION);
    }
}
