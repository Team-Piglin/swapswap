package piglin.swapswap.domain.membercoupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponQueryRepository {

    void deleteAllByIsUsedIsTrueAndModifiedTimeBefore(LocalDateTime fourteenDaysAgo);

    Optional<MemberCoupon> findByIdAndIsUsedFalse(Long couponId);
}
