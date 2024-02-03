package piglin.swapswap.domain.chatroom.service;

import java.util.List;
import piglin.swapswap.domain.chatroom.dto.ChatRoomResponseDto;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.message.dto.request.MessageRequestDto;
import piglin.swapswap.domain.message.dto.response.MessageResponseDto;

public interface ChatRoomService {

    ChatRoomResponseDto getChatRoomResponseDto(String roomId, Long memberId);

    List<ChatRoomResponseDto> getChatRoomList(Member member);

    List<MessageResponseDto> getMessageByChatRoomId(String roomId, Member member);

    String createChatroom(Member member, Long secondMemberId);

    void saveMessage(MessageRequestDto requestDto);

    void leaveChatRoom(Member member, String roomId);
}