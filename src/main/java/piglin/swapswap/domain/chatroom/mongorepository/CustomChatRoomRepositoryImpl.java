package piglin.swapswap.domain.chatroom.mongorepository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import piglin.swapswap.domain.chatroom.entity.ChatRoom;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.message.dto.request.MessageRequestDto;

@RequiredArgsConstructor
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public ChatRoom getChatRoom(String roomId) {

        Query query = new Query(where("id").is(roomId).and("isDeleted").is(false));

        return mongoTemplate.findOne(query, ChatRoom.class);
    }

    @Override
    public List<ChatRoom> getChatRoomList(Member member) {

        Query query = new Query(
                where("isDeleted").is(false)
                        .orOperator(
                                where("firstMemberId").is(member.getId()).and("isLeaveFirstMember").is(false),
                                where("secondMemberId").is(member.getId()).and("isLeaveSecondMember").is(false)
                        )
        );

        return mongoTemplate.find(query,ChatRoom.class);
    }

    @Override
    public ChatRoom getChatRoomByMemberIds(Long firstMemberId, Long secondMemberId) {

        Query query = query(where("isDeleted").is(false)
                .orOperator(
                        where("firstMemberId").is(firstMemberId).and("secondMemberId").is(secondMemberId),
                        where("secondMemberId").is(firstMemberId).and("firstMemberId").is(secondMemberId)
                ));

        return mongoTemplate.findOne(query, ChatRoom.class);
    }

    @Override
    public void reentryMember(Member member, ChatRoom chatRoom) {

        Update update = new Update();

        if (member.getId().equals(chatRoom.getFirstMemberId()) && chatRoom.isLeaveFirstMember()) {

            update.set("isLeaveFirstMember", false);
        }

        if (member.getId().equals(chatRoom.getSecondMemberId()) && chatRoom.isLeaveSecondMember()) {

            update.set("isLeaveSecondMember", false);
        }

        if (!update.getUpdateObject().isEmpty()) {

            Query query = new Query(Criteria.where("id").is(chatRoom.getId()));
            mongoTemplate.updateFirst(query, update, ChatRoom.class);
        }
    }

    @Override
    public void updateChatRoom(MessageRequestDto requestDto) {

        Query updateQuery = new Query(Criteria.where("id").is(requestDto.roomId()));

        Update update = new Update()
                .set("lastMessage", requestDto.text())
                .set("lastMessageTime", LocalDateTime.now())
                .set("isLeaveFirstMember", false)
                .set("isLeaveSecondMember", false);

        mongoTemplate.updateFirst(updateQuery, update, ChatRoom.class);
    }

    @Override
    public void leaveChatRoomOrDeleteChatRoom(ChatRoom chatRoom) {

        mongoTemplate.save(chatRoom);
    }

}
