package piglin.swapswap.domain.daelwallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import piglin.swapswap.domain.daelwallet.entity.DealWallet;
import piglin.swapswap.domain.daelwallet.mapper.DealWalletMapper;
import piglin.swapswap.domain.daelwallet.repository.DealWalletRepository;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.wallet.service.WalletService;
import piglin.swapswap.domain.wallethistory.constant.HistoryType;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.dealwallet.DealWalletNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealWalletServiceImplV1 implements DealWalletService {

    private final DealWalletRepository dealWalletRepository;
    private final WalletService walletService;

    @Override
    @SwapLog
    @Transactional
    public void createDealWallet(Deal deal, Member member, Long totalFee) {

        log.info("dealId: {} | memberId: {}", deal.getId(), member.getId());

        DealWallet dealWallet = DealWalletMapper.createDealWallet(deal, member, totalFee);

        log.info("createdDealWallet - dealWalletDealId: {} | requestSwapMoney: {} | receiveSwapMoney: {}",
                dealWallet.getDeal().getId(), dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());

        walletService.withdrawSwapMoney(totalFee, HistoryType.TEMPORARY_WITHDRAW, member.getId());

        dealWalletRepository.save(dealWallet);
        log.info("savedDealWalletId: {}", dealWallet.getId());
    }

    @Override
    @Transactional
    public boolean existDealWalletByDealId(Long dealId) {

        return dealWalletRepository.existsByDealId(dealId);
    }

    @Override
    @SwapLog
    @Transactional
    public void updateDealWallet(Deal deal, Member member, Long totalFee) {

        log.info("dealId: {} | memberId: {}", deal.getId(), member.getId());

        DealWallet dealWallet = dealWalletRepository.findByDealId(deal.getId())
                .orElseThrow(DealWalletNotFoundException::new);
        boolean isRequestMember = deal.getRequestMemberbill().getMember().getId().equals(member.getId());
        boolean isReceiveMember = deal.getReceiveMemberbill().getMember().getId().equals(member.getId());
        log.info("dealWalletId: {} | requestSwapMoney: {} | receiveSwapMoney: {} | memberIsRequestMember: {} | memberIsReceiveMember: {}",
                dealWallet.getId(), dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney(), isReceiveMember, isReceiveMember);

        if (isRequestMember) {
            dealWallet.updateRequestMemberSwapMoney(totalFee);
        }
        if (isReceiveMember) {
            dealWallet.updateReceiveMemberSwapMoney(totalFee);
        }
        log.info("requestSwapMoneyAfterUpdate: {} | receiveSwapMoneyAfterUpdate: {}",
                dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());

        walletService.withdrawSwapMoney(totalFee, HistoryType.TEMPORARY_WITHDRAW, member.getId());
    }

    @Override
    @SwapLog
    public void withdrawMemberSwapMoneyAtComplete(Deal deal) {

        DealWallet dealWallet = dealWalletRepository.findByDealId(deal.getId())
                .orElseThrow(DealWalletNotFoundException::new);

        log.info("dealWalletId: {} | dealWalletRequestSwapMoney: {}, dealWalletReceiveSwapMoney: {}",
                dealWallet.getId(), dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());

        if (dealWallet.getRequestMemberSwapMoney() != null) {
            Long requestMemberTotalFee = dealWallet.getRequestMemberSwapMoney() - deal.getRequestMemberbill().getCommission();
            log.info("requestMemberTotalFee: {}", requestMemberTotalFee);
            walletService.depositSwapMoney(requestMemberTotalFee,
                    HistoryType.DEAL_DEPOSIT, deal.getReceiveMemberbill().getMember().getId());
        }
        if (dealWallet.getReceiveMemberSwapMoney() != null) {
            Long receiveMemberTotalFee = dealWallet.getReceiveMemberSwapMoney() - deal.getReceiveMemberbill().getCommission();
            log.info("receiveMemberTotalFee: {}", receiveMemberTotalFee);
            walletService.depositSwapMoney(receiveMemberTotalFee,
                    HistoryType.DEAL_DEPOSIT, deal.getRequestMemberbill().getMember().getId());
        }
    }

    @Override
    @SwapLog
    public void rollbackTemporarySwapMoney(Deal deal) {

        log.info("dealId: {}", deal.getId());
        DealWallet dealWallet = dealWalletRepository.findByDealId(deal.getId())
                .orElseThrow(DealWalletNotFoundException::new);
        log.info("dealWalletId: {} | originalDealWalletRequestSwapMoney: {} | originalDealWalletReceiveSwapMoney: {}",
                dealWallet.getId(), dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());

        if (dealWallet.getRequestMemberSwapMoney() != null) {
            walletService.depositSwapMoney(dealWallet.getRequestMemberSwapMoney(),
                    HistoryType.CANCEL_WITHDRAW, deal.getRequestMemberbill().getMember().getId());
            log.info("updatedDealWalletRequestSwapMoney: {} | updatedDealWalletReceiveSwapMoney: {}",
                    dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());
        }
        if (dealWallet.getReceiveMemberSwapMoney() != null) {
            walletService.depositSwapMoney(dealWallet.getReceiveMemberSwapMoney(),
                    HistoryType.CANCEL_WITHDRAW, deal.getReceiveMemberbill().getMember().getId());
            log.info("updatedDealWalletRequestSwapMoney: {} | updatedDealWalletReceiveSwapMoney: {}",
                    dealWallet.getRequestMemberSwapMoney(), dealWallet.getReceiveMemberSwapMoney());
        }

        dealWalletRepository.delete(dealWallet);
    }
}
