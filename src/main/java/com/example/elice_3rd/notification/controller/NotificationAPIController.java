package com.example.elice_3rd.notification.controller;

import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.dto.NotificationLoginResponseDto;
import com.example.elice_3rd.notification.dto.NotificationResponseDto;
import com.example.elice_3rd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationAPIController {

    private final NotificationService notificationService;

    @GetMapping("/is-login")
    public ResponseEntity<NotificationLoginResponseDto> getMember(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            String loggedInUserId = principal.getName();
            log.debug("Logged In User ID: {}", loggedInUserId);

            NotificationLoginResponseDto loginStatus = notificationService.isLogin(loggedInUserId);
            return ResponseEntity.ok(loginStatus);
        } catch (Exception e) {
            log.error("Error occurred while fetching login status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 멤버별 확인되지 않은 알림 가져오기
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotificationsForMember(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            Long loggedInUserId = notificationService.findByMemberId(principal.getName());
            log.debug("Logged In User ID: {}", loggedInUserId);

            List<NotificationResponseDto> unreadNotifications = notificationService.getUnreadNotificationsForMember(loggedInUserId);
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            log.error("Error occurred while fetching unread notifications: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // SSE로 알림 수신
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getNotifications(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            Long loggedInUserId = notificationService.findByMemberId(principal.getName());
            log.debug("Logged In User ID: {}", loggedInUserId);

            SseEmitter emitter = notificationService.addEmitter(loggedInUserId);
            return ResponseEntity.ok(emitter);
        } catch (Exception e) {
            log.error("Error occurred while connecting SSE: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 알림 확인 상태 변경
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Void> notificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.notificationAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error occurred while marking notification as read: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
