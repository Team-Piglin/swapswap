package piglin.swapswap.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import piglin.swapswap.domain.chatroom.service.ChatRoomServiceImpl;
import piglin.swapswap.domain.message.dto.request.MessageRequestDto;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatRoomServiceImpl chatRoomService;
    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/chat/message")
    public void enter(MessageRequestDto message) {

        chatRoomService.saveMessage(message);

        String destination = "/queue/chat/room" + message.roomId();
        sendingOperations.convertAndSend(destination, message);
    }
}