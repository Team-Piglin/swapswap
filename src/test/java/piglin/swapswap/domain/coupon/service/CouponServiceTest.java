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

    static int COUPON_COUNT = 5;
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
    @DisplayName("비관적 락을 사용하여 잔여 쿠폰이 5장이고 1000명의 유저가 동시에 쿠폰을 발급받으려는 상황")
    void issueCouponByPessimisticLock() throws InterruptedException {

        //given
        final int PARTICIPATION_PEOPLE = 1000;

        CountDownLatch countDownLatch = new CountDownLatch(PARTICIPATION_PEOPLE);
        List<ParticipateWorkerByPessimisticLock> workers = Stream.generate(
                        () -> new ParticipateWorkerByPessimisticLock(member, countDownLatch))
                .limit(PARTICIPATION_PEOPLE)
                .toList();

        // when
        List<Thread> threads = workers.stream().map(Thread::new).toList();

        threads.forEach(Thread::start);
        countDownLatch.await();

        // then
        Coupon coupon = couponRepository.findById(couponId).get();
        Assertions.assertEquals(0, coupon.getCount());
    }

    private class ParticipateWorkerByPessimisticLock implements Runnable {

        private Member member;
        private CountDownLatch countDownLatch;

        public ParticipateWorkerByPessimisticLock(Member member, CountDownLatch countDownLatch) {
            this.member = member;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            couponService.issueEventCouponByPessimisticLock(couponId, member);
            countDownLatch.countDown();
        }
    }

    @Test
    @DisplayName("낙관적 락을 사용하여 잔여 쿠폰이 5장이고 1000명의 유저가 동시에 쿠폰을 발급받으려는 상황")
    void issueCouponByOptimisticLock() throws InterruptedException {

        //given
        final int PARTICIPATION_PEOPLE = 1000;

        CountDownLatch countDownLatch = new CountDownLatch(PARTICIPATION_PEOPLE);
        List<ParticipateWorkerByOptimisticLock> workers = Stream.generate(
                        () -> new ParticipateWorkerByOptimisticLock(member, countDownLatch))
                .limit(PARTICIPATION_PEOPLE)
                .toList();

        // when
        List<Thread> threads = workers.stream().map(Thread::new).toList();

        threads.forEach(Thread::start);
        countDownLatch.await();

        // then
        Coupon coupon = couponRepository.findById(couponId).get();
        Assertions.assertEquals(0, coupon.getCount());
    }

    private class ParticipateWorkerByOptimisticLock implements Runnable {

        private Member member;
        private CountDownLatch countDownLatch;

        public ParticipateWorkerByOptimisticLock(Member member, CountDownLatch countDownLatch) {
            this.member = member;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            couponService.issueEventCouponByPessimisticLock(couponId, member);
            countDownLatch.countDown();
        }
    }

}