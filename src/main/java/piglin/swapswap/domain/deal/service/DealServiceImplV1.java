package piglin.swapswap.domain.deal.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.bill.service.BillService;
import piglin.swapswap.domain.billcoupon.dto.BillCouponResponseDto;
import piglin.swapswap.domain.billcoupon.service.BillCouponService;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;
import piglin.swapswap.domain.billpost.service.BillPostService;
import piglin.swapswap.domain.daelwallet.service.DealWalletService;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.deal.dto.request.DealCreateRequestDto;
import piglin.swapswap.domain.deal.dto.response.DealDetailResponseDto;
import piglin.swapswap.domain.deal.dto.response.DealGetReceiveDto;
import piglin.swapswap.domain.deal.dto.response.DealGetRequestDto;
import piglin.swapswap.domain.deal.dto.response.DealHistoryResponseDto;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.deal.mapper.DealMapper;
import piglin.swapswap.domain.deal.repository.DealRepository;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.service.MemberService;
import piglin.swapswap.domain.notification.constant.NotificationType;
import piglin.swapswap.domain.notification.service.NotificationService;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.exception.deal.InvalidDealRequestException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealServiceImplV1 implements DealService {

    private final DealRepository dealRepository;
    private final BillService billService;
    private final BillPostService billPostService;
    private final BillCouponService billCouponService;
    private final MemberService memberService;
    private final NotificationService notificationService;
    private final DealWalletService dealWalletService;

    @Override
    @SwapLog
    @Transactional
    public Long createDeal(Member member, DealCreateRequestDto requestDto) {

        log.info("createDeal - requestMemberId: {}", member.getId());
        if (requestDto.requestPostIdList().isEmpty() && requestDto.receivePostIdList().isEmpty()) {
            throw new InvalidDealRequestException(ErrorCode.BOTH_POST_ID_LIST_EMPTY_EXCEPTION);
        }

        Bill requestMemberBill = billService.createBill(member, requestDto.requestMemberExtraFee(),
                requestDto.requestPostIdList());

        Member receiveMember = memberService.getMember(requestDto.receiveMemberId());

        Bill receiveMemberBill = billService.createBill(receiveMember, requestDto.receiveMemberExtraFee(),
                requestDto.receivePostIdList());

        Deal deal = DealMapper.createDeal(requestMemberBill, receiveMemberBill);
        log.info("BillIdAfterCreateDeal - requestMemberBillId: {} | receiveMemberBillId: {}", deal.getRequestMemberbill().getId(), deal.getReceiveMemberbill().getId());

        dealRepository.save(deal);
        log.info("dealId: {}", deal.getId());

        String Url = "http://swapswap.shop/deals/" + deal.getId();
        String content = receiveMember.getNickname()+"님! 거래 요청이 왔어요!";
        notificationService.send(receiveMember, NotificationType.DEAL,content,Url);

        return deal.getId();
    }

    @Override
    public List<DealGetRequestDto> getMyRequestDealList(Long memberId) {

        List<Deal> myRequestDealList = dealRepository.findAllMyRequestDeal(memberId);

        return DealMapper.toDealGetRequestDtoList(myRequestDealList);
    }

    @Override
    public List<DealGetReceiveDto> getMyReceiveDealList(Long memberId) {

        List<Deal> myReceiveDealList = dealRepository.findAllMyReceiveDeal(memberId);

        return DealMapper.toDealGetReceiveDtoList(myReceiveDealList);
    }

    @Override
    public DealDetailResponseDto getDeal(Long dealId, Member member) {

        Deal deal = dealRepository.findDealByIdWithBillAndMember(dealId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_DEAL_EXCEPTION)
        );

        Bill requestMemberBill = deal.getRequestMemberbill();
        Bill receiveMemberBill = deal.getReceiveMemberbill();

        if (!requestMemberBill.getMember().getId().equals(member.getId())
                &&!receiveMemberBill.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.NOT_CONTAIN_DEAL_MEMBER_EXCEPTION);
        }

        List<BillPostResponseDto> requestBillPostDtoList = billPostService.getBillPostDtoList(
                requestMemberBill);
        List<BillPostResponseDto> receiveBillPostDtoList = billPostService.getBillPostDtoList(
                receiveMemberBill);

        List<BillCouponResponseDto> requestBillCouponDtoList = billCouponService.getBillCouponDtoList(
                requestMemberBill);
        List<BillCouponResponseDto> receiveBillCouponDtoList = billCouponService.getBillCouponDtoList(
                receiveMemberBill);

        return DealMapper.toDealDetailResponseDto(deal, requestBillPostDtoList,
                receiveBillPostDtoList, requestBillCouponDtoList, receiveBillCouponDtoList);
    }

    @Override
    @Transactional
    public void bothAllowThenChangeDealing(Long billId) {

        Deal deal = getDealByBillIdWithBill(billId);

        if(deal.getRequestMemberbill().getIsAllowed() && deal.getReceiveMemberbill().getIsAllowed()) {

            deal.updateDealStatus(DealStatus.DEALING);
        }
    }

    @Override
    @SwapLog
    @Transactional
    public void bothTakeThenChangeCompleted(Long billId) {

        Deal deal = getDealByBillIdWithBill(billId);
        log.info("dealId: {} | originalDealStatus: {}", deal.getId(), deal.getDealStatus());

        if(deal.getRequestMemberbill().getIsTaked() && deal.getReceiveMemberbill().getIsTaked()) {

            deal.updateDealStatus(DealStatus.COMPLETED);
            deal.completedTime();
            log.info("dealId: {} | updatedDealStatus: {} | updatedDealCompletedTime: {}", deal.getId(), deal.getDealStatus(), deal.getCompletedDealTime());

            if(dealWalletService.existDealWalletByDealId(deal.getId())) {
                dealWalletService.withdrawMemberSwapMoneyAtComplete(deal);
            }
        }
    }

    @Override
    public List<DealHistoryResponseDto> getDealHistoryList(Long memberId) {

        List<Deal> myAllDealList = dealRepository.findAllMyDeal(memberId);

        return DealMapper.getDealHistory(myAllDealList);
    }

    @Override
    public Long getDealIdByBillId(Long billId) {

        Deal deal = dealRepository.findDealByBillId(billId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_DEAL_EXCEPTION)
        );

        return deal.getId();
    }

    @Override
    public Deal getDealByBillIdWithBill(Long billId) {

        return dealRepository.findByBillIdWithBill(billId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_DEAL_EXCEPTION)
        );
    }

    @Override
    public Deal getDealByBillIdWithBillAndMember(Long billId) {

        return dealRepository.findByBillIdWithBillAndMember(billId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_DEAL_EXCEPTION)
        );
    }

    @Override
    public void isDifferentMember(Member member, Long receiveMemberId) {

        if (member.getId().equals(receiveMemberId)) {
            throw new BusinessException(ErrorCode.REQUEST_ONLY_DIFFERENT_USER_EXCEPTION);
        }
    }
}
