package piglin.swapswap.domain.message.mongorepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import piglin.swapswap.domain.message.entity.Message;

@RequiredArgsConstructor
public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void deleteMessageByRoomId(String roomId) {

        Query query = new Query(Criteria.where("roomId").is(roomId));

        Update update = new Update().set("isDeleted", true);

        mongoTemplate.updateMulti(query, update, Message.class);
    }
}