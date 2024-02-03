package piglin.swapswap.domain.notification.service;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.notification.constant.NotificationType;
import piglin.swapswap.domain.notification.dto.NotificationCountDto;
import piglin.swapswap.domain.notification.dto.NotificationResponseDto;
import piglin.swapswap.domain.notification.entity.Notification;
import piglin.swapswap.domain.notification.mapper.NotificationMapper;
import piglin.swapswap.domain.notification.repository.EmitterRepository;
import piglin.swapswap.domain.notification.repository.EmitterRepositoryImpl;
import piglin.swapswap.domain.notification.repository.NotificationRepository;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;

    @Override
    public SseEmitter subscribe(Long memberId, String lastEventId) {

        String emitterId = memberId + "_" + System.currentTimeMillis();

        Long timeout = 60L * 1000L * 60L;

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        if (!lastEventId.isEmpty()) {
            sendLostData(lastEventId, memberId, emitterId, emitter);
        }

        return emitter;
    }

    public void sendLostData(String lastEventId, Long memberId, String emitterId,
            SseEmitter emitter) {

        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithById(
                String.valueOf(memberId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId,
                        entry.getValue()));
    }

    public void sendNotification(SseEmitter emitter, String eventId, String emitterId,
            Object data) {

        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            log.error("SSE 연결 실패");
            emitterRepository.deleteById(emitterId);
        }
    }

    @Async
    public void send(Member receiver, NotificationType notificationType, String content,
            String url) {

        Notification notification = notificationRepository.save(
                createNotification(receiver, notificationType, content, url));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithById(
                receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key,
                            NotificationMapper.createResponseDto(notification));
                }
        );
    }

    public Notification createNotification(Member receiver, NotificationType notificationType,
            String content, String url) {

        return NotificationMapper.createNotification(receiver, notificationType, content, url);
    }

    @Transactional
    public List<NotificationResponseDto> findAllNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findAllByMemberId(memberId);

        return notifications.stream()
                .map(NotificationMapper::createResponseDto)
                .collect(Collectors.toList());
    }

    public NotificationCountDto countUnReadNotifications(Long memberId) {
        Long count = notificationRepository.countUnReadNotifications(memberId);
        return NotificationCountDto.builder()
                .count(count)
                .build();

    }

    @Transactional
    public void readNotification(Long notificationId) {

        Notification checkedNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_NOTIFICATION));

        checkedNotification.read();

        notificationRepository.save(checkedNotification);
    }

    @Transactional
    public void deleteAllByNotifications(Long memberId) {

        notificationRepository.deleteAllByReceiverId(memberId);
    }

    @Transactional
    public void deleteByNotifications(Long notificationId) {

        notificationRepository.deleteById(notificationId);
    }
}
