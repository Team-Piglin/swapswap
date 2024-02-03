package piglin.swapswap.domain.deal.dto.response;

import lombok.Builder;
import piglin.swapswap.domain.deal.constant.DealStatus;

@Builder
public record DealGetRequestDto(
        Long dealId,
        String receiveMemberNickname,
        DealStatus dealStatus
) {

}
