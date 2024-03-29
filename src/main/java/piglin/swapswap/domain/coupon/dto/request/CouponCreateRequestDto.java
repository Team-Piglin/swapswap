package piglin.swapswap.domain.coupon.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import piglin.swapswap.domain.coupon.constant.CouponType;

@Builder
public record CouponCreateRequestDto(@NotBlank @Length(min = 3, max = 60) String couponName,
                                     CouponType couponType,
                                     @Min(1) @Max(100) int discountPercentage,
                                     LocalDateTime expiredTime,
                                     @Min(1) int couponCount) {

}
