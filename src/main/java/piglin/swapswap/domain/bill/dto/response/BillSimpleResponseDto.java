package piglin.swapswap.domain.bill.dto.response;

import lombok.Builder;

@Builder
public record BillSimpleResponseDto (
        Long id,
        Long extraFee,
        Long commission
){

}
