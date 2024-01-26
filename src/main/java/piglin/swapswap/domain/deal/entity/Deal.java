package piglin.swapswap.domain.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.deal.constant.DealStatus;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deal extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime completedDealTime;

    @Column
    private DealStatus dealStatus;

    @JoinColumn(name = "first_member_bill_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Bill firstMemberbill;

    @JoinColumn(name = "second_member_bill_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Bill secondMemberbill;
}
