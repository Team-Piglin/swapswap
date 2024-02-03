package piglin.swapswap.domain.daelwallet.mapper;

import piglin.swapswap.domain.daelwallet.entity.DealWallet;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.Member;

public class DealWalletMapper {

    public static DealWallet createDealWallet(Deal deal, Member member, Long totalFee) {

        DealWallet dealWallet = DealWallet.builder()
                                          .deal(deal)
                                          .RequestMemberSwapMoney(null)
                                          .ReceiveMemberSwapMoney(null)
                                          .build();

        if (deal.getRequestMemberbill().getMember().getId().equals(member.getId())) {
            dealWallet.updateRequestMemberSwapMoney(totalFee);
        }

        if (deal.getReceiveMemberbill().getMember().getId().equals(member.getId())) {
            dealWallet.updateReceiveMemberSwapMoney(totalFee);
        }

        return dealWallet;
    }

}
