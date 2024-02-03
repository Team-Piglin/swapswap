package piglin.swapswap.domain.billcoupon.service;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billcoupon.dto.BillCouponResponseDto;
import piglin.swapswap.domain.billcoupon.dto.RedeemCouponRequestDto;
import piglin.swapswap.domain.member.entity.Member;

public interface BillCouponService {

    List<BillCouponResponseDto>  getBillCouponDtoList(Bill bill);

    void redeemCoupons(Long billId, Member member, RedeemCouponRequestDto dealRedeemCouponRequestDto);

    void initialBillCouponList(Long billId);
}
