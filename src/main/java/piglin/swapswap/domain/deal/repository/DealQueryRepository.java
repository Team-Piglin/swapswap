package piglin.swapswap.domain.deal.repository;

import java.util.List;
import java.util.Optional;
import piglin.swapswap.domain.deal.entity.Deal;

public interface DealQueryRepository {

    List<Deal> findAllMyRequestDeal(Long memberId);

    List<Deal> findAllMyReceiveDeal(Long memberId);

    List<Deal> findAllMyDeal(Long memberId);

    Optional<Deal> findDealByIdWithBillAndMember(Long dealId);

    Optional<Deal> findDealByBillId(Long billId);

    Optional<Deal> findByBillIdWithBillAndMember(Long billId);

    Optional<Deal> findByBillIdWithBill(Long billId);
}
