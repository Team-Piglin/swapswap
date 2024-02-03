package piglin.swapswap.domain.coupon.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import piglin.swapswap.domain.coupon.dto.request.CouponCreateRequestDto;
import piglin.swapswap.domain.coupon.dto.response.CouponGetResponseDto;
import piglin.swapswap.domain.coupon.entity.Coupon;
import piglin.swapswap.domain.coupon.mapper.CouponMapper;
import piglin.swapswap.domain.coupon.repository.CouponRepository;
import piglin.swapswap.domain.coupon.validator.CouponValidator;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.membercoupon.service.MemberCouponService;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImplV2 implements CouponService {

    private final CouponRepository couponRepository;

    private final MemberCouponService memberCouponService;

    @Override
    public Long createCoupon(CouponCreateRequestDto couponCreateRequestDto) {

        CouponValidator.validateExpiredTime(couponCreateRequestDto.expiredTime());

        Coupon coupon = CouponMapper.createCoupon(couponCreateRequestDto);

        Coupon savedCoupon = couponRepository.save(coupon);

        return savedCoupon.getId();
    }

    @Override
    public int getCouponCount(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));

        return coupon.getCount();
    }

    @Override
    @Transactional
    public void issueEventCoupon(Long couponId, Member member) {

        log.info("\ncouponIssueStart - Member: {} | couponId: {} | transactionActive: {}", member.getEmail(), couponId,
                TransactionSynchronizationManager.isActualTransactionActive());

        Coupon coupon = couponRepository.findByIdWithPessimisticLock(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));
        log.info("\ncouponName: {} | couponType: {} | couponCountBeforeIssue: {}", coupon.getName(), coupon.getCouponType(), coupon.getCount());

        log.info("\nissueCouponPossible: {}", issueCouponPossible(coupon));
        if (issueCouponPossible(coupon)) {
            memberCouponService.saveMemberCoupon(member, coupon);
            coupon.issueCoupon();
            log.info("\ncouponCountAfterIssue: {}", coupon.getCount());
        }
    }

    public boolean issueCouponPossible(Coupon coupon) {

        return coupon.getCount() > 0;
    }

    @Override
    public CouponGetResponseDto getCouponDetail(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));

        return CouponMapper.couponToGetResponseDto(coupon);
    }

}
