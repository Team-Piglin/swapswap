package piglin.swapswap.domain.wallet.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.service.MemberService;
import piglin.swapswap.domain.wallet.entity.Wallet;
import piglin.swapswap.domain.wallet.repository.WalletRepository;
import piglin.swapswap.domain.wallethistory.constant.HistoryType;
import piglin.swapswap.domain.wallethistory.entity.WalletHistory;
import piglin.swapswap.domain.wallethistory.mapper.WalletHistoryMapper;
import piglin.swapswap.domain.wallethistory.service.WalletHistoryService;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.exception.wallet.InvalidWithdrawException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImplV1 implements WalletService {

    private final WalletRepository walletRepository;

    private final WalletHistoryService walletHistoryService;

    private final MemberService memberService;


    @Override
    public Wallet createWallet() {

        return walletRepository.save(Wallet.createWallet());
    }

    @Override
    @SwapLog
    @Transactional
    public void depositSwapMoney(Long depositSwapMoney, HistoryType historyType, Long memberId) {

        Member member = memberService.getMemberWithWallet(memberId);
        Wallet wallet = member.getWallet();
        log.info("originalWalletMoney : {} | depositSwapMoney: {}", wallet.getSwapMoney(), depositSwapMoney);

        wallet.depositSwapMoney(depositSwapMoney);
        log.info("updatedWalletMoney: {}", wallet.getSwapMoney());

        recordWalletHistory(wallet, depositSwapMoney, historyType);
    }

    @Override
    @SwapLog
    @Transactional
    public void withdrawSwapMoney(Long withdrawSwapMoney, HistoryType historyType, Long memberId) {

        Member member = memberService.getMemberWithWallet(memberId);
        Wallet wallet = member.getWallet();

        log.info("originalWalletMoney : {} | withdrawSwapMoney: {}", wallet.getSwapMoney(), withdrawSwapMoney);
        if (impossibleWithdrawSwapMoney(wallet, withdrawSwapMoney)) {
            throw new InvalidWithdrawException(ErrorCode.LACK_OF_SWAP_MONEY_EXCEPTION);
        }
        wallet.withdrawSwapMoney(withdrawSwapMoney);
        log.info("updatedWalletMoney: {}", wallet.getSwapMoney());

        recordWalletHistory(wallet, withdrawSwapMoney, historyType);
    }

    private boolean impossibleWithdrawSwapMoney(Wallet wallet, Long withdrawSwapMoney) {

        return wallet.getSwapMoney() < withdrawSwapMoney;
    }

    private void recordWalletHistory(Wallet wallet, Long swapMoney, HistoryType historyType) {

        WalletHistory walletHistory = WalletHistoryMapper.createWalletHistory(wallet, swapMoney,
                historyType);

        walletHistoryService.recordWalletHistory(walletHistory);
    }
}
