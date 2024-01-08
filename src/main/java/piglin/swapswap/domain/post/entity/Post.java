package piglin.swapswap.domain.post.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.constant.Category;


@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Type(JsonType.class)
    @Column(nullable = false, columnDefinition = "json")
    private Map<String, Object> imageUrl;

    @Column(nullable = false)
    private Long viewCnt;

    @Column(nullable = false)
    private Long upCnt;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

}
