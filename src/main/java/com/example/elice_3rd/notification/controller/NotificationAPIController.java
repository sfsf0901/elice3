package com.example.elice_3rd.notification.controller;

import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.dto.NotificationLoginResponseDto;
import com.example.elice_3rd.notification.dto.NotificationResponseDto;
import com.example.elice_3rd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//        String loggedInUserId = principal.getName();
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        String loggedInUserId = "3213@323213";
        log.debug("Logged In User ID: {}", loggedInUserId);
        //
        NotificationLoginResponseDto loginStatus = notificationService.isLogin(loggedInUserId);
        return ResponseEntity.ok(loginStatus);
    }

    // 멤버별 확인되지 않은 알림 가져오기
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotificationsForMember(Principal principal) {
//        Long loggedInUserId = chatService.findByMemberId(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        Long loggedInUserId = 2L;
        log.debug("Logged In User ID: {}", loggedInUserId);
        //
        List<NotificationResponseDto> unreadNotifications = notificationService.getUnreadNotificationsForMember(loggedInUserId);
        return ResponseEntity.ok(unreadNotifications);
    }

    // SSE로 알림 수신
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getNotifications(Principal principa) {
//        Long loggedInUserId = notificationService.findByMemberId(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        Long loggedInUserId = 2L;
        log.debug("Logged In User ID: {}", loggedInUserId);
        //

        return notificationService.addEmitter(loggedInUserId);
    }


    // 알림 확인 상태 변경
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Void> notificationAsRead(@PathVariable Long notificationId) {
        notificationService.notificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
