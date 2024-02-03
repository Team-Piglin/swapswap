package piglin.swapswap.domain.bill.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import piglin.swapswap.domain.bill.dto.request.BillUpdateRequestDto;
import piglin.swapswap.domain.bill.dto.response.BillSimpleResponseDto;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.bill.mapper.BillMapper;
import piglin.swapswap.domain.bill.repository.BillRepository;
import piglin.swapswap.domain.billpost.service.BillPostService;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.service.PostService;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.bill.BillNotFoundException;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.exception.common.UnauthorizedModifyException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillServiceImplV1 implements BillService {

    private final BillRepository billRepository;
    private final BillPostService billPostService;

    @SwapLog
    @Override
    public Bill createBill(Member member, Long extraFee, List<Long> postIdList) {

        log.info("createBill - memberId: {}", member.getId());

        Bill bill = BillMapper.createBill(extraFee, member);
        billRepository.save(bill);

        log.info("billId: {} | billExtraFee: {} | billCommission: {} | billMemberId: {}",
                bill.getId(), bill.getExtrafee(), bill.getCommission(), bill.getMember().getId());

        if (!postIdList.isEmpty()) {
            billPostService.createBillPost(bill, postIdList);
        }

        return bill;
    }

    @Override
    public BillSimpleResponseDto getMyBillDto(Long billId) {

        Bill bill = billRepository.findById(billId).orElseThrow(BillNotFoundException::new);

        return BillMapper.billToSimpleResponseDto(bill);
    }

    @Override
    public Bill getMyBill(Long billId){

        return billRepository.findById(billId)
                .orElseThrow(BillNotFoundException::new);
    }

    @Override
    @SwapLog
    @Transactional
    public void updateUsedSwapPay(Long billId, Member member) {

        log.info("memberId: {}", member.getId());
        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        log.info("billMemberId: {} | memberId: {}", bill.getMember().getId(), member.getId());

        validateModifyAuthority(bill.getMember(), member);

        log.info("originalBillSwapMoneyUsed: {}", bill.getIsSwapMoneyUsed());
        bill.updateUsedSwapMoney();
        log.info("updatedBillSwapMoneyUsed: {}", bill.getIsSwapMoneyUsed());
    }

    @Override
    @SwapLog
    @Transactional
    public void updateBillAllowWithoutSwapPay(Long billId, Member member) {

        log.info("memberId: {}", member.getId());
        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        log.info("originalBillAllowStatus: {}", bill.getIsAllowed());

        validateModifyAuthority(bill.getMember(), member);

        bill.updateAllow();
        log.info("updatedBillAllowStatus: {}", bill.getIsAllowed());
        billPostService.updatePostListDealStatusByBill(bill);
    }

    @Override
    @Transactional
    public void initialCommission(Long billId, Member member) {

        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);

        validateModifyAuthority(bill.getMember(), member);

        if (bill.getCommission()==null) {
            bill.initialCommission();
        }
    }

    @Override
    @SwapLog
    public void updateBillAllowTrueWithSwapPay(Long billId, Member member) {

        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        validateModifyAuthority(bill.getMember(), member);

        log.info("memberId: {} | originalBillAllowed: {}", member.getId(), bill.getIsAllowed());

        bill.updateAllow();
        log.info("originalBillAllowed: {}", bill.getIsAllowed());

        billPostService.updatePostListDealStatusByBill(bill);
    }

    @Override
    @SwapLog
    public void updateBillAllowFalseWithSwapPay(Long billId, Member member) {

        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        validateModifyAuthority(bill.getMember(), member);

        log.info("memberId: {} | originalBillAllowed: {} | originalBillCommission: {}",
                member.getId(), bill.getIsAllowed(), bill.getCommission());

        bill.updateAllow();
        bill.initialCommission();
        log.info("updatedBillAllowed: {} | updatedBillCommission: {}",
                bill.getIsAllowed(), bill.getCommission());

        billPostService.updatePostListDealStatusByBill(bill);
    }

    @Override
    public void validateUpdateBill(Deal deal, Long billId, Member member) {

        Long requestMemberId = deal.getRequestMemberbill().getMember().getId();
        Long receiveMemberId = deal.getReceiveMemberbill().getMember().getId();

        if (!requestMemberId.equals(member.getId()) && !receiveMemberId.equals(member.getId())) {
            throw new UnauthorizedModifyException(ErrorCode.UNAUTHORIZED_MODIFY_DEAL_EXCEPTION);
        }

        if (!deal.getDealStatus().equals(DealStatus.REQUESTED)) {
            throw new BusinessException(ErrorCode.CAN_NOT_UPDATE_CAUSE_DEAL_IS_NOT_REQUESTED);
        }

        Bill bill = null;

        if (deal.getRequestMemberbill().getId().equals(billId)) {
            bill = deal.getRequestMemberbill();
        }
        if (deal.getReceiveMemberbill().getId().equals(billId)) {
            bill = deal.getReceiveMemberbill();
        }

        if (bill.getIsAllowed()) {
            throw new BusinessException(ErrorCode.CAN_NOT_UPDATE_BILL_POST_LIST_CAUSE_IS_NOT_REQUESTED);
        }
    }

    @SwapLog
    @Override
    public void updateBill(Member member, Long billId, Long memberId, BillUpdateRequestDto requestDto) {

        log.info("loggedMemberId: {}", member.getId());

        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        log.info("originalBillExtraFee: {}", bill.getExtrafee());

        bill.updateExtraFee(requestDto.extraFee());
        log.info("updatedBillExtraFee: {}", bill.getExtrafee());
    }

    @Override
    @SwapLog
    public Long getTotalFee(Long billId) {

        Bill bill = billRepository.findById(billId).orElseThrow(
                BillNotFoundException::new);
        log.info("billExtraFee: {} | BillCommission: {}", bill.getExtrafee(), bill.getCommission());
        return bill.getExtrafee() + bill.getCommission();
    }

    @Override
    @SwapLog
    @Transactional
    public void updateBillTake(Long billId, Member member) {

        log.info("memberId: {}", member.getId());
        Bill bill = billRepository.findByIdWithMember(billId).orElseThrow(
                BillNotFoundException::new);
        validateModifyAuthority(bill.getMember(), member);
        log.info("originalBillTake: {}", bill.getIsTaked());

        bill.updateTake();
        log.info("updatedBillTake: {}", bill.getIsTaked());

        billPostService.updatePostListDealStatusByBill(bill);
    }

    private void validateModifyAuthority(Member billMember, Member loginMember) {

        if (!billMember.getId().equals(loginMember.getId())) {
            throw new UnauthorizedModifyException(ErrorCode.UNAUTHORIZED_MODIFY_DEAL_EXCEPTION);
        }
    }
}
