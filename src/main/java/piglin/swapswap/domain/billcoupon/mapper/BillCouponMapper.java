package piglin.swapswap.domain.billcoupon.mapper;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billcoupon.dto.BillCouponResponseDto;
import piglin.swapswap.domain.billcoupon.entity.BillCoupon;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;

public class BillCouponMapper {

    public static List<BillCouponResponseDto> toBillCouponResponseDtoList(
            List<MemberCoupon> memberCouponList) {

        return memberCouponList.stream().map(memberCoupon -> BillCouponResponseDto.builder()
                .name(memberCoupon.getCoupon().getName())
                .build()).toList();
    }

    public static BillCoupon createBillCoupon(Bill bill, MemberCoupon memberCoupon) {

        return BillCoupon.builder().bill(bill).memberCoupon(memberCoupon).build();
    }
}
