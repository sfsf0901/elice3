package com.example.elice_3rd.notification.controller;

import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/send-notification/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public NotificationDto sendNotificationToChatRoom(@Payload NotificationDto notificationDto, @DestinationVariable Long chatRoomId) {
        // 알림을 채팅방에 전달
        notificationService.sendNotification(chatRoomId, notificationDto.getSender(), notificationDto.getMessage());
        return notificationDto;
    }

    // 멤버별 확인되지 않은 알림 가져오기
    @GetMapping("/unread/{memberId}")
    public ResponseEntity<List<NotificationDto>> getUnreadNotificationsForUser(@PathVariable Long memberId) {
        List<NotificationDto> unreadNotifications = notificationService.getUnreadNotificationsForMember(memberId);
        return ResponseEntity.ok(unreadNotifications);
    }

    // 알림 확인 상태 변경
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Void> notificationAsRead(@PathVariable Long notificationId) {
        notificationService.notificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
