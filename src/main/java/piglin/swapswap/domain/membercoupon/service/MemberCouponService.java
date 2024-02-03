package piglin.swapswap.domain.membercoupon.service;

import java.util.List;
import piglin.swapswap.domain.coupon.entity.Coupon;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.membercoupon.dto.response.MyCouponGetResponseDto;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;

public interface MemberCouponService {

    void saveMemberCoupon(Member member, Coupon coupon);

    List<MyCouponGetResponseDto> getMycouponList(Member member);

    MemberCoupon getMemberCouponWithCouponById(Long memberCouponId);

    MemberCoupon getMemberCouponWithCouponByMemberCouponId(Long couponId);

    void deleteAllMemberCouponByMember(Member loginMember);

    void reRegisterCouponByMember(Member loginMember);

    Long getCountByCouponId(Long couponId);
}
