package com.example.elice_3rd.notification.service;

import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import com.example.elice_3rd.chat.entity.mysql.ChatRoom;
import com.example.elice_3rd.chat.entity.mysql.MemberStatus;
import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import com.example.elice_3rd.chat.service.KafkaProducer;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.dto.NotificationLoginResponseDto;
import com.example.elice_3rd.notification.dto.NotificationResponseDto;
import com.example.elice_3rd.notification.entity.Notification;
import com.example.elice_3rd.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaProducer kafkaProducer;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public Long findByMemberId (String memberId){
        Member member = memberRepository.findByEmail(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        return member.getMemberId();
    }

    public NotificationLoginResponseDto isLogin(String memberId){
        Member member = memberRepository.findByEmail(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        NotificationLoginResponseDto responseDto = new NotificationLoginResponseDto();
        if (member == null) {
            responseDto.setIsLogin(false);
        } else {
            responseDto.setIsLogin(true);
        }
        return responseDto;
    }

    // 알림 생성 및  Kafka로 알림 전송
    @Transactional
    public void sendNotificationToKafka(ChatMessage chatMessage){
        try {
            // 채팅방의 모든 멤버 조회
            ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                    .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

            for (Member receiver : chatRoom.getMembers()) {
                if (receiver.getMemberId() != chatMessage.getSenderId()) { // 보낸 사람 제외
                    MemberStatus memberStatus = memberStatusRepository.findByChatRoomChatRoomIdAndMemberMemberId(chatMessage.getChatRoomId(), receiver.getMemberId())
                            .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

                    // 오프라인 상태인 경우에만 알림 생성
                    if (memberStatus.getStatus() == MemberStatusType.OFFLINE) {
                        Notification notification = new Notification();
                        notification.setChatRoomId(chatRoom);
                        notification.setReceiverId(receiver);
                        notification.setChatMessageId(chatMessage.getChatMessageId());
                        notification.setMessage(chatMessage.getMessage());
                        notificationRepository.save(notification);
                        log.info("Notification sent to user " + receiver.getMemberId());

                        NotificationDto notificationDto = NotificationDto.toDto(notification);

                        // Kafka 메시지 전송, 실패 시 롤백
                        kafkaProducer.sendMessage(notificationDto)
                                .exceptionally(e -> {
                                    log.error("Failed to send message to Kafka: {}", chatMessage, e);
                                    throw new RuntimeException("Transaction rollback due to Kafka message transmission failure.", e);
                                })
                                .join(); // 동기적으로 처리하여 예외 발생 시 트랜잭션 롤백
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error processing notification message: {}", chatMessage.getMessage(), e);
            throw new RuntimeException("Error processing notification message", e);
        }
    }

    // 새로운 emitter 추가
    public SseEmitter addEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> {
            log.info("Emitter for member {} completed", memberId);
            emitters.remove(memberId);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter for member {} timed out", memberId);
            emitters.remove(memberId);
        });
        emitter.onError((e) -> {
            log.error("Error occurred in emitter for member {}", memberId, e);
            emitters.remove(memberId);
        });

        return emitter;
    }

    // 새로운 알림을 클라이언트로 전송
    public void sendNotification(NotificationDto notificationDto) {
        Long receiverId = notificationDto.getReceiverId();
        boolean isValid = isValidReceiver(notificationDto);
        if (isValid) {
            // 수신자가 유효한 경우 알림 전송
            SseEmitter emitter = emitters.get(receiverId);
            if (emitter != null) {
                try {
                    emitter.send(notificationDto);
                } catch (IOException e) {
                    log.error("Error sending notification to member {}", receiverId, e);
                    emitter.completeWithError(e);
                    emitters.remove(receiverId);
                }
            } else {
                log.warn("No emitter found for member {}", receiverId);
            }
        } else {
            log.warn("Received notification is not for this member.");
        }
    }

    // 사용자가 수신자가 맞는지 확인
    public boolean isValidReceiver(NotificationDto notificationDto) {
        Long receiverId = notificationDto.getReceiverId();
        Member member = getCurrentMember();
        Long memberId = member.getMemberId();

        if (!receiverId.equals(memberId)){
            return false;
        }
        return chatRoomRepository.existsByMembers_MemberId(memberId);
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("No authenticated Member.");
        }

        String email = authentication.getName();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
    }

    // 알림 읽음 상태 변화
    @Transactional
    public void notificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notification.setReadStatus(true);
        notificationRepository.save(notification);
        log.info("Notification with ID {} marked as read", notificationId);
    }

    // 멤버별 확인되지 않은 알림 목록
    public List<NotificationResponseDto> getUnreadNotificationsForMember(Long memberId) {
        // 멤버의 읽지 않은 알림 목록 조회
        List<Notification> unreadNotifications = notificationRepository.findByReceiverId_memberIdAndReadStatus(
                memberId, false);

        return unreadNotifications.stream()
                .map(NotificationResponseDto::toDto)
                .collect(Collectors.toList());
    }
}
