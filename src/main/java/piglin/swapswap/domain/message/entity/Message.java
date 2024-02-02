package piglin.swapswap.domain.message.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.message.constant.MessageType;

@Document("message")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseTime {

    @Id
    private String id;

    private String roomId;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String text;

    private Boolean isDeleted;
}