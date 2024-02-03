package piglin.swapswap.domain.deal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record DealCreateRequestDto (

        @PositiveOrZero Long requestMemberExtraFee,
        @PositiveOrZero Long receiveMemberExtraFee,
        List<Long> requestPostIdList,
        List<Long> receivePostIdList,
        @NotNull Long receiveMemberId
) {
}
