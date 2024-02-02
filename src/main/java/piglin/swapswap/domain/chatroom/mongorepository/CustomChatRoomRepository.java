package piglin.swapswap.domain.chatroom.mongorepository;

import java.util.List;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.message.dto.request.MessageRequestDto;

public interface CustomChatRoomRepository {

    ChatRoom getChatRoom(String roomId);

    List<ChatRoom> getChatRoomList(Member member);

    ChatRoom getChatRoomByMemberIds(Long firstMemberId, Long secondMemberId);

    void reentryMember(Member member, ChatRoom chatRoom);

    void updateChatRoom(MessageRequestDto requestDto);

    void leaveChatRoomOrDeleteChatRoom(ChatRoom chatRoom);
}
