package com.example.elice_3rd.notification.service;

import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.MemberStatus;
import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.entity.Notification;
import com.example.elice_3rd.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    /*구현할 기능
    1. 채팅방 내에서 오프라인시에 새로운 메세지를 받으면 알림 발송 처리
    2. 알림 확인시에 알림 상태 변화 처리
    3. 확인 안한 알림 메세지 목록 조회
    */

    private final ChatRoomRepository chatRoomRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final NotificationRepository notificationRepository;


    // 알림 발송
    @Transactional
    public void sendNotification(Long chatRoomId, Long senderId, String message) {
        // 채팅방의 모든 멤버 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        for (Member receiver : chatRoom.getMembers()) {
            if (receiver.getMemberId() != senderId) { // 보낸 사람 제외
                MemberStatus memberStatus = memberStatusRepository.findByChatRoomIdAndMemberId(chatRoomId, receiver.getMemberId())
                        .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

                // 오프라인 상태인 경우에만 알림 생성
                if (memberStatus.getStatus() == MemberStatusType.OFFLINE) {
                    Notification notification = new Notification();
                    notification.setChatRoom(chatRoom);
                    notification.setReceiver(receiver);
                    notification.setMessage(message);
                    notification.setReadStatus(false); // 기본적으로 읽지 않음
                    notificationRepository.save(notification);
                    log.info("Notification sent to user " + receiver.getMemberId());
                }
            }
        }
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
    public List<NotificationDto> getUnreadNotificationsForMember(Long memberId) {
        // 멤버의 읽지 않은 알림 목록 조회
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIdAndReadStatus(
                memberId, false);

        return unreadNotifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getNotificationId(),
                        notification.getReceiver().getMemberId(),
                        notification.getMessage(),
                        notification.getReadStatus()))
                .collect(Collectors.toList());
    }
}
