package piglin.swapswap.domain.deal.mapper;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billcoupon.dto.BillCouponResponseDto;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.deal.dto.response.DealDetailResponseDto;
import piglin.swapswap.domain.deal.dto.response.DealGetReceiveDto;
import piglin.swapswap.domain.deal.dto.response.DealGetRequestDto;
import piglin.swapswap.domain.deal.dto.response.DealHistoryResponseDto;
import piglin.swapswap.domain.deal.entity.Deal;

public class DealMapper {

    public static Deal createDeal(Bill requestMemberbill, Bill receiveMemberbill) {

        return Deal.builder()
                .dealStatus(DealStatus.REQUESTED)
                .requestMemberbill(requestMemberbill)
                .receiveMemberbill(receiveMemberbill)
                .build();
    }

    public static List<DealGetRequestDto> toDealGetRequestDtoList(List<Deal> myRequestDealList) {

        return myRequestDealList.stream().map(deal -> DealGetRequestDto.builder()
                .dealId(deal.getId())
                .receiveMemberNickname(deal.getReceiveMemberbill().getMember().getNickname())
                .dealStatus(deal.getDealStatus())
                .build()).toList();
    }


    public static List<DealGetReceiveDto> toDealGetReceiveDtoList(List<Deal> myReceiveDealList) {

        return myReceiveDealList.stream().map(deal -> DealGetReceiveDto.builder()
                .dealId(deal.getId())
                .requestMemberNickname(deal.getRequestMemberbill().getMember().getNickname())
                .dealStatus(deal.getDealStatus())
                .build()).toList();
    }

    public static DealDetailResponseDto toDealDetailResponseDto(
            Deal deal,
            List<BillPostResponseDto> requestBillPostListDto,
            List<BillPostResponseDto> receiveBillPostListDto,
            List<BillCouponResponseDto> requestBillCouponDtoList,
            List<BillCouponResponseDto> receiveBillCouponDtoList) {

        Bill requestMemberBill = deal.getRequestMemberbill();
        Bill receiveMemberBill = deal.getReceiveMemberbill();

        return DealDetailResponseDto.builder()
                .id(deal.getId())
                .dealStatus(deal.getDealStatus())
                .requestMemberBillId(requestMemberBill.getId())
                .receiveMemberBillId(receiveMemberBill.getId())
                .requestMemberId(requestMemberBill.getMember().getId())
                .receiveMemberId(receiveMemberBill.getMember().getId())
                .requestMemberNickname(requestMemberBill.getMember().getNickname())
                .receiveMemberNickname(receiveMemberBill.getMember().getNickname())
                .requestDealPostList(requestBillPostListDto)
                .receiveDealPostList(receiveBillPostListDto)
                .requestMemberExtraFee(deal.getRequestMemberbill().getExtrafee())
                .receiveMemberExtraFee(deal.getReceiveMemberbill().getExtrafee())
                .requestAllow(deal.getRequestMemberbill().getIsAllowed())
                .receiveAllow(deal.getReceiveMemberbill().getIsAllowed())
                .requestTake(deal.getRequestMemberbill().getIsTaked())
                .receiveTake(deal.getReceiveMemberbill().getIsTaked())
                .useSwapMoneyRequestMember(deal.getRequestMemberbill().getIsSwapMoneyUsed())
                .useSwapMoneyReceiveMember(deal.getReceiveMemberbill().getIsSwapMoneyUsed())
                .requestMemberCommission(deal.getRequestMemberbill().getCommission())
                .receiveMemberCommission(deal.getReceiveMemberbill().getCommission())
                .requestCouponList(requestBillCouponDtoList)
                .receiveCouponList(receiveBillCouponDtoList)
                .build();
    }

    public static List<DealHistoryResponseDto> getDealHistory(List<Deal> dealList) {

        return dealList.stream().map(deal ->
                        DealHistoryResponseDto.builder()
                                .id(deal.getId())
                                .dealStatus(deal.getDealStatus())
                                .createdTime(deal.getCreatedTime())
                                .completedDealTime(deal.getCompletedDealTime())
                                .build())
                .toList();
    }
}
