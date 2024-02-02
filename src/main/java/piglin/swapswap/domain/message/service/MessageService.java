package piglin.swapswap.domain.message.service;

import java.util.List;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.message.entity.Message;

public interface MessageService {

    List<Message> getMessageByRoomId(String roomId);

    void saveMessage(Message message);

    void deleteMessage(ChatRoom chatRoom);
}
