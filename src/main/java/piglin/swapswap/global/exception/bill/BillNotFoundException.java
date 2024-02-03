package piglin.swapswap.global.exception.bill;

import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

public class BillNotFoundException extends BusinessException {

    public BillNotFoundException() {super(ErrorCode.NOT_FOUND_BILL_EXCEPTION);}
}
