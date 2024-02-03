package piglin.swapswap.domain.message.mongorepository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import piglin.swapswap.domain.message.entity.Message;

public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {

    List<Message> findAllByRoomId(String roomId);

}
