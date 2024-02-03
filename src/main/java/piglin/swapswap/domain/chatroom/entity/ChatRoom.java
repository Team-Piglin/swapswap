package piglin.swapswap.domain.chatroom.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.mongodb.core.mapping.Document;
import piglin.swapswap.domain.common.BaseTime;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

@Document("chatroom")
@Getter
@Builder
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTime {

    @Id
    private String id;

    private Long firstMemberId;

    private Long secondMemberId;

    private boolean isLeaveFirstMember;

    private boolean isLeaveSecondMember;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private Boolean isDeleted;

    public void deleteChatRoom() {
        isDeleted = true;
    }

    public void leaveChatRoom(Member member) {

        if (member.getId().equals(firstMemberId)) {

            isLeaveFirstMember = true;

        } else if (member.getId().equals(secondMemberId)) {

            isLeaveSecondMember = true;

        } else {

            throw new BusinessException(ErrorCode.NOT_CHAT_ROOM_MEMBER_EXCEPTION);
        }
    }
}
