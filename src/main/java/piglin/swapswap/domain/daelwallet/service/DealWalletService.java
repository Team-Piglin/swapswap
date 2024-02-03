package piglin.swapswap.domain.daelwallet.service;

import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.Member;

public interface DealWalletService {

     void createDealWallet(Deal deal, Member member, Long totalFee);

     void updateDealWallet(Deal deal, Member member, Long totalFee);

     void withdrawMemberSwapMoneyAtComplete(Deal deal);

     void rollbackTemporarySwapMoney(Deal deal);

     boolean existDealWalletByDealId(Long dealId);
}
