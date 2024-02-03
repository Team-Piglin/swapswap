package piglin.swapswap.domain.chatroom.mapper;

import lombok.RequiredArgsConstructor;
import piglin.swapswap.domain.chatroom.dto.ChatRoomResponseDto;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.member.entity.Member;

@RequiredArgsConstructor
public class ChatRoomMapper {

    public static ChatRoom createChatRoom(Member firstMember, Long secondMemberId) {

        return ChatRoom.builder()
                .firstMemberId(firstMember.getId())
                .isLeaveFirstMember(false)
                .secondMemberId(secondMemberId)
                .isLeaveSecondMember(false)
                .isDeleted(false)
                .build();
    }

    public static ChatRoomResponseDto getChatRoomResponseDto(ChatRoom chatRoom, String nickname) {

        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .nickname(nickname)
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .build();
    }

}