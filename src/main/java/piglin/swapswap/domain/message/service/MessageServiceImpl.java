package piglin.swapswap.domain.message.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.message.entity.Message;
import piglin.swapswap.domain.message.mongorepository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public List<Message> getMessageByRoomId(String roomId) {

        return messageRepository.findAllByRoomId(roomId);
    }

    @Override
    public void saveMessage(Message message) {

        messageRepository.insert(message);
    }

    @Override
    public void deleteMessage(ChatRoom chatRoom) {

        messageRepository.deleteMessageByRoomId(chatRoom.getId());
    }
}
