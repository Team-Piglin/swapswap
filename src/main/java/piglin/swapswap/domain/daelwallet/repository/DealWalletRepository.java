package piglin.swapswap.domain.daelwallet.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import piglin.swapswap.domain.daelwallet.entity.DealWallet;

public interface DealWalletRepository extends JpaRepository<DealWallet, Long> {

    boolean existsByDealId(Long dealId);

    Optional<DealWallet> findByDealId(Long dealId);
}
