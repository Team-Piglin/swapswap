package piglin.swapswap.domain.message.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.message.entity.Message;
import piglin.swapswap.domain.message.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public List<Message> findAllByChatRoomId(Long roomId) {

        return messageRepository.findAllByChatRoom_Id(roomId);
    }

    @Override
    public void saveMessage(Message message) {

        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(ChatRoom chatRoom) {

        messageRepository.deleteMessage(chatRoom);
    }
}
