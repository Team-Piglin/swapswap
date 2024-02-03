package piglin.swapswap.domain.billcoupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_coupon_id", nullable = false)
    private MemberCoupon memberCoupon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

}
