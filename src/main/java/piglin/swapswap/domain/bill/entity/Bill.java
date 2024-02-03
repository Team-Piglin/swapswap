package piglin.swapswap.domain.bill.entity;

import static piglin.swapswap.domain.bill.constant.BillConstant.SWAP_PAY_COMMISSION_PERCENT;

import jakarta.persistence.Column;
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
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.member.entity.Member;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bill extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long extrafee;

    @Column
    private Long commission;

    @Column(nullable = false)
    private Boolean isAllowed;

    @Column(nullable = false)
    private Boolean isTaked;

    @Column(nullable = false)
    private Boolean isSwapMoneyUsed;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateAllow() {

        isAllowed = !isAllowed;
    }

    public void updateUsedSwapMoney() {

        isSwapMoneyUsed = !isSwapMoneyUsed;
    }

    public void updateTake() {

        isTaked = true;
    }

    public void discountCommission(int commissionDiscountPercent) {

        commission -= (long) (commission * commissionDiscountPercent * 0.01);
    }

    public void initialCommission() {

        commission = (long)(extrafee * SWAP_PAY_COMMISSION_PERCENT);
    }

    public void updateExtraFee(Long extraFee) {

        this.extrafee = extraFee;
        this.commission = (long)(extrafee * SWAP_PAY_COMMISSION_PERCENT);
    }
}
