package piglin.swapswap.domain.billpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.billpost.entity.BillPost;

public interface BillPostRepository extends JpaRepository<BillPost, Long>, BillPostQueryRepository {

}
