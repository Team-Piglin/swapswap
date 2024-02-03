package piglin.swapswap.domain.deal.dto.response;

import java.util.List;
import lombok.Builder;
import piglin.swapswap.domain.billcoupon.dto.BillCouponResponseDto;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;
import piglin.swapswap.domain.deal.constant.DealStatus;

@Builder
public record DealDetailResponseDto(

    Long id,

    DealStatus dealStatus,

    Long requestMemberBillId,

    Long receiveMemberBillId,

    Long requestMemberId,

    Long receiveMemberId,

    String requestMemberNickname,

    String receiveMemberNickname,

    List<BillPostResponseDto> requestDealPostList,

    List<BillPostResponseDto> receiveDealPostList,

    Long requestMemberExtraFee,

    Long receiveMemberExtraFee,

    Boolean requestAllow,

    Boolean receiveAllow,

    Boolean requestTake,

    Boolean receiveTake,

    Boolean useSwapMoneyRequestMember,

    Boolean useSwapMoneyReceiveMember,

    Long requestMemberCommission,

    Long receiveMemberCommission,

    List<BillCouponResponseDto> requestCouponList,

    List<BillCouponResponseDto> receiveCouponList
    ) {
}
