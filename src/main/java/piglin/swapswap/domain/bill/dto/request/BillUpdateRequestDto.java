package piglin.swapswap.domain.bill.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record BillUpdateRequestDto(
        @PositiveOrZero
        Long extraFee,
        List<Long> postIdList
) {

}
