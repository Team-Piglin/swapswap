package piglin.swapswap.domain.deal.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import piglin.swapswap.domain.deal.constant.DealStatus;

@Builder
public record DealHistoryResponseDto(
        Long id,
        DealStatus dealStatus,
        LocalDateTime createdTime,
        LocalDateTime completedDealTime
) {

}
