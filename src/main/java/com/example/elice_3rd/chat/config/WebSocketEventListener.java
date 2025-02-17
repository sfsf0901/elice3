package com.example.elice_3rd.chat.config;

import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatService chatService;

    // WebSocket 세션이 종료되었을 때 발생하는 이벤트 처리
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        // STOMP 메시지를 래핑하여, 메시지의 헤더 정보에 접근
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) {
            log.error("User not found, unable to update status.");
            throw new IllegalStateException("User information is missing in WebSocket session.");
        }

        // 연결이 끊어진 유저의 ID를 가져옴
        String userName = accessor.getUser().getName();
        Long memberId = Long.valueOf(userName); // 유저 이름 또는 ID를 가져오는 방법

        // WebSocket 연결이 종료되었을 때 상태를 OFFLINE으로 변경
        chatService.updateMemberStatusToOffline(memberId);
        log.info("User with ID {} has disconnected, status updated to OFFLINE.", memberId);
    }
}
