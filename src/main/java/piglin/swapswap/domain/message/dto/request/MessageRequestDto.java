package piglin.swapswap.domain.message.dto.request;

import lombok.Builder;
import piglin.swapswap.domain.message.constant.MessageType;

@Builder
public record MessageRequestDto (
        String roomId,
        Long senderId,
        MessageType type,
        String text
) {

}

