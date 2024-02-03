package piglin.swapswap.domain.bill.billfacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import piglin.swapswap.domain.bill.dto.request.BillUpdateRequestDto;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.bill.service.BillService;
import piglin.swapswap.domain.billcoupon.service.BillCouponService;
import piglin.swapswap.domain.billpost.service.BillPostService;
import piglin.swapswap.domain.daelwallet.service.DealWalletService;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.deal.service.DealService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.global.annotation.SwapLog;

@Component
@Slf4j
@RequiredArgsConstructor
public class BillFacade {

    private final DealService dealService;
    private final DealWalletService dealWalletService;
    private final BillService billService;
    private final BillPostService billPostService;
    private final BillCouponService billCouponService;

    @SwapLog
    @Transactional
    public void updateBillAllowTrueWithSwapPay(Long billId, Member member) {

        Deal deal = dealService.getDealByBillIdWithBill(billId);
        billService.updateBillAllowTrueWithSwapPay(billId, member);
        Long totalFee = billService.getTotalFee(billId);
        log.info("totalFee: {}", totalFee);
        Boolean isExistDealWallet = dealWalletService.existDealWalletByDealId(deal.getId());
        log.info("isExistDealWallet: {}", isExistDealWallet);

        if (isExistDealWallet) {
            dealWalletService.updateDealWallet(deal, member, totalFee);
        } else {
            dealWalletService.createDealWallet(deal, member, totalFee);
        }
    }

    @SwapLog
    @Transactional
    public void updateBillAllowFalseWithSwapPay(Long billId, Member member) {

        Deal deal = dealService.getDealByBillIdWithBill(billId);
        billService.updateBillAllowFalseWithSwapPay(billId, member);
        dealWalletService.rollbackTemporarySwapMoney(deal);
        billCouponService.initialBillCouponList(billId);
    }

    @Transactional
    public void validateUpdateBill(Long billId, Member member) {

        Deal deal = dealService.getDealByBillIdWithBillAndMember(billId);
        billService.validateUpdateBill(deal, billId, member);
    }

    @SwapLog
    @Transactional
    public void updateBill(Member member, Long billId, Long memberId,
            BillUpdateRequestDto requestDto) {

        Bill bill = billService.getMyBill(billId);
        Deal deal = dealService.getDealByBillIdWithBillAndMember(billId);
        billService.validateUpdateBill(deal, billId, member);
        billService.updateBill(member, billId, memberId, requestDto);
        billPostService.deleteAllByBill(bill);
        billPostService.createBillPost(bill, requestDto.postIdList());
    }
}