package piglin.swapswap.domain.membercoupon.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import piglin.swapswap.domain.coupon.entity.Coupon;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.membercoupon.dto.response.MyCouponGetResponseDto;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;
import piglin.swapswap.domain.membercoupon.mapper.MemberCouponMapper;
import piglin.swapswap.domain.membercoupon.repository.MemberCouponRepository;
import piglin.swapswap.global.exception.membercoupon.MemberCouponNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCouponServiceImplV1 implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;

    @Override
    public void saveMemberCoupon(Member member, Coupon coupon) {

        MemberCoupon memberCoupon = MemberCouponMapper.createMemberCoupon(member, coupon);
        log.info("\nmemberCouponSave - member: {}, memberCouponName: {}, memberCouponType: {}",
                memberCoupon.getMember().getEmail(), memberCoupon.getCoupon().getName(), memberCoupon.getCoupon().getCouponType());

        memberCouponRepository.save(memberCoupon);
    }

    @Override
    public List<MyCouponGetResponseDto> getMycouponList(Member member) {

        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberIdAndIsUsedIsFalseWithCoupon(
                member.getId());

        return MemberCouponMapper.memberCouponListToMyCouponResponseDtoList(memberCouponList);
    }

    public void deleteAllMemberCouponByMember(Member loginMember) {
        memberCouponRepository.deleteAllMemberCouponByMember(loginMember);
    }

    @Override
    public void reRegisterCouponByMember(Member loginMember) {
        memberCouponRepository.reRegisterCouponByMember(loginMember);
    }

    @Override
    public Long getCountByCouponId(Long couponId) {

        return memberCouponRepository.countByCouponId(couponId);
    }

    @Override
    public MemberCoupon getMemberCouponWithCouponById(Long memberCouponId) {
        return memberCouponRepository.findByIdAndIsUsedIsFalseWithCoupon(memberCouponId)
                .orElseThrow(MemberCouponNotFoundException::new);
    }

    @Override
    public MemberCoupon getMemberCouponWithCouponByMemberCouponId(Long memberCouponId) {

        return memberCouponRepository.findByMemberCouponIdAndIsUsedIsFalseWithCoupon(memberCouponId)
                .orElseThrow(MemberCouponNotFoundException::new);
    }

}
