package piglin.swapswap.domain.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private DealStatus dealStatus;

    @Column
    private LocalDateTime completedDealTime;

    @JoinColumn(name = "request_member_bill_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Bill requestMemberbill;

    @JoinColumn(name = "receive_member_bill_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Bill receiveMemberbill;

    public void updateDealStatus(DealStatus dealStatus) {

        this.dealStatus = dealStatus;
    }

    public void completedTime() {

        completedDealTime = LocalDateTime.now();
    }
}
