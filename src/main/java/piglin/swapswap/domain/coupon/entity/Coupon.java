package piglin.swapswap.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.coupon.constant.CouponType;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(nullable = false)
    private CouponType couponType;

    @Column(nullable = false)
    private int discountPercentage;

    @Column(nullable = false)
    private LocalDateTime expired_time;

    @Column
    private Long userId;

}
