package piglin.swapswap.domain.chatroom.mongorepository;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>, CustomChatRoomRepository {

    void deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(LocalDateTime fourteenDaysAgo);
}
