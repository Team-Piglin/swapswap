package piglin.swapswap.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import piglin.swapswap.TCIntegrationTest;
import org.springframework.data.redis.core.RedisTemplate;
import piglin.swapswap.domain.coupon.constant.CouponType;
import piglin.swapswap.domain.coupon.dto.request.CouponCreateRequestDto;
import piglin.swapswap.domain.coupon.entity.Coupon;
import piglin.swapswap.domain.coupon.repository.CouponRepository;
import piglin.swapswap.domain.member.constant.MemberRole;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.repository.MemberRepository;
import piglin.swapswap.domain.membercoupon.service.MemberCouponService;
import piglin.swapswap.domain.wallet.entity.Wallet;
import piglin.swapswap.domain.wallet.repository.WalletRepository;

@SpringBootTest
class CouponServiceTest extends TCIntegrationTest {

    @Autowired
    CouponService couponService;

    @Autowired
    MemberCouponService memberCouponService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    static int COUPON_COUNT = 100;
    static Long couponId;
    static Member member;
    static Wallet wallet;

    @BeforeEach
    void init() {

        CouponCreateRequestDto requestDto = CouponCreateRequestDto.builder()
                .couponName("수수료 할인 쿠폰")
                .couponType(CouponType.FEE)
                .discountPercentage(50)
                .expiredTime(LocalDateTime.now().plusHours(1L))
                .couponCount(COUPON_COUNT)
                .build();

        couponId = couponService.createCoupon(requestDto);

        wallet = Wallet.builder()
                .swapMoney(1L)
                .isDeleted(false)
                .build();

        walletRepository.save(wallet);

        member = Member.builder()
                .email("yeowuli2@naver.com")
                .nickname("손창현")
                .role(MemberRole.USER)
                .isDeleted(false)
                .wallet(wallet)
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("잔여 쿠폰이 5장이고 1000명의 유저가 동시에 쿠폰을 발급받으려는 상황")
    void issueCouponTest() throws InterruptedException {

        //given
        final int PARTICIPATION_PEOPLE = 1000;

        CountDownLatch countDownLatch = new CountDownLatch(PARTICIPATION_PEOPLE);
        List<ParticipateWorkerByRedis> workers = Stream.generate(
                        () -> new ParticipateWorkerByRedis(member, countDownLatch))
                .limit(PARTICIPATION_PEOPLE)
                .toList();

        // when
        List<Thread> threads = workers.stream().map(Thread::new).toList();

        threads.forEach(Thread::start);
        countDownLatch.await();

        // then
        Coupon coupon = couponRepository.findById(couponId).get();
        Long issueCouponCount = memberCouponService.getCountByCouponId(couponId);
        Assertions.assertEquals(COUPON_COUNT, issueCouponCount);
    }

    private class ParticipateWorkerByRedis implements Runnable {

        private Member member;
        private CountDownLatch countDownLatch;

        public ParticipateWorkerByRedis(Member member, CountDownLatch countDownLatch) {
            this.member = member;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            couponService.issueEventCoupon(couponId, member);
            countDownLatch.countDown();
        }
    }

}