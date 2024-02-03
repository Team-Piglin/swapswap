package piglin.swapswap.domain.billcoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.billcoupon.entity.BillCoupon;

public interface BillCouponRepository extends JpaRepository<BillCoupon, Long>, BillCouponQueryRepository {

    void deleteAllByBillId(Long billId);

    boolean existsByBillId(Long billId);
}
