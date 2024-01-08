package piglin.swapswap.domain.deal.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
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

    @Column(nullable = false)
    private Long firstUserId;

    @Column(nullable = false)
    private Long secondUserId;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<Long, Object> firstPostIdList;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<Long, Object> secondPostIdList;

    @Column(nullable = false)
    private int firstExtraFee;

    @Column(nullable = false)
    private int secondExtraFee;

    @Column(nullable = false)
    private Boolean firstAllow;

    @Column(nullable = false)
    private Boolean secondAllow;

    @Column(nullable = false)
    private Boolean firstTake;

    @Column(nullable = false)
    private Boolean secondTake;

    @Column
    private LocalDateTime completedDealTime;

}

