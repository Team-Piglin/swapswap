package piglin.swapswap.domain.coupon.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
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
@Primary
@RequiredArgsConstructor
public class CouponServiceImplV3 implements CouponService{

    private final CouponRepository couponRepository;

    private final MemberCouponService memberCouponService;

    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Long createCoupon(CouponCreateRequestDto couponCreateRequestDto) {

        CouponValidator.validateExpiredTime(couponCreateRequestDto.expiredTime());

        Coupon coupon = CouponMapper.createCoupon(couponCreateRequestDto);

        Coupon savedCoupon = couponRepository.save(coupon);

        redisTemplate.opsForValue().set(coupon.getName(), 0);

        return savedCoupon.getId();
    }

    @Override
    public int getCouponCount(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));

        int couponNumber = redisTemplate.opsForValue().get(coupon.getName());

        return coupon.getCount() - couponNumber;
    }

    @Override
    @Transactional
    public void issueEventCoupon(Long couponId, Member member) {

        log.info("\ncouponIssueStart - Member: {} | couponId: {} | transactionActive: {}", member.getEmail(), couponId,
                TransactionSynchronizationManager.isActualTransactionActive());

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));
        log.info("\ncouponName: {} | couponType: {} | couponCountBeforeIssue: {}", coupon.getName(), coupon.getCouponType(), coupon.getCount());

        Long couponNumber = redisTemplate.opsForValue().increment(coupon.getName());

        log.info("\nissueCouponPossible: {}", issueCouponPossible(coupon.getCount(), couponNumber));
        if (issueCouponPossible(coupon.getCount(), couponNumber)) {
            memberCouponService.saveMemberCoupon(member, coupon);
            log.info("\ncouponCountAfterIssue: {}", coupon.getCount());
        }
    }

    public boolean issueCouponPossible(int couponCount, Long couponNumber) {

        return couponCount >= couponNumber;
    }

    @Override
    public CouponGetResponseDto getCouponDetail(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_COUPON_EXCEPTION));

        return CouponMapper.couponToGetResponseDto(coupon);
    }

}
