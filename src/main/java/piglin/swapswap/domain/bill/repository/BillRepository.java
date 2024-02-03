package piglin.swapswap.domain.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.bill.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long>, BillQueryRepository {

}
