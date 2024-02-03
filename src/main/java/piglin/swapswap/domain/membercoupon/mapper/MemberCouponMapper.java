package piglin.swapswap.domain.membercoupon.mapper;

import java.util.List;
import piglin.swapswap.domain.coupon.entity.Coupon;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.membercoupon.dto.response.MyCouponGetResponseDto;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;

import static java.util.stream.Collectors.toList;

public class MemberCouponMapper {

    public static MemberCoupon createMemberCoupon(Member member, Coupon coupon) {

        return MemberCoupon.builder()
                .coupon(coupon)
                .member(member)
                .isUsed(false)
                .build();
    }

    public static List<MyCouponGetResponseDto> memberCouponListToMyCouponResponseDtoList(
            List<MemberCoupon> memberCouponList) {

        return memberCouponList.stream().map(memberCoupon ->
        {
            Coupon coupon = memberCoupon.getCoupon();
            return MyCouponGetResponseDto.builder()
                    .couponId(memberCoupon.getId())
                    .couponName(coupon.getName())
                    .couponType(coupon.getCouponType())
                    .discountPercentage(coupon.getDiscountPercentage())
                    .expiredTime(coupon.getExpiredTime())
                    .build();
        }).toList();
    }
}
