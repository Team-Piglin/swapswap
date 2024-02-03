package piglin.swapswap.domain.billpost.dto;

import lombok.Builder;
import piglin.swapswap.domain.deal.constant.DealStatus;

@Builder
public record BillPostResponseDto(
        Long postId,
        String postTile,
        String imageUrl,
        DealStatus postStatus
) {

}
