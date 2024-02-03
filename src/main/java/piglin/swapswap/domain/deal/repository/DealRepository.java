package piglin.swapswap.domain.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.deal.entity.Deal;

public interface DealRepository extends JpaRepository<Deal, Long>, DealQueryRepository {
}
