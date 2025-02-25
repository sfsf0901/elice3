package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.dto.*;
import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatAPIController {

    private final ChatService chatService;

    // 채팅방 연결
    @PostMapping("/check-chat-room")
    public ResponseEntity<ChatRoomResponseDto> checkChatRoom(@RequestBody ChatRoomRequestDto request, Principal principal) {
//        Long loggedInUserId = chatService.findByMemberId(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);
//
//        Long doctorUserId  = request.getMemberIds().stream()
//                .filter(id -> !id.equals(loggedInUserId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Invalid Doctor User ID"));
//        log.debug("Doctor User ID: {}", doctorUserId );

        // 테스트용
        Long loggedInUserId = 1L;
        log.debug("Logged In User ID: {}", loggedInUserId);

        Long doctorUserId = 2L;
        log.debug("Doctor User ID: {}", doctorUserId);
        //

        request.setMemberIds(new HashSet<>(Arrays.asList(loggedInUserId, doctorUserId)));

        ChatRoomResponseDto chatRoomDto = chatService.checkChatRoom(request, loggedInUserId);
        return ResponseEntity.ok(chatRoomDto);

    }

    // 특정 멤버가 속한 모든 채팅방 목록 조회
    @GetMapping("/chat-rooms")
    public ResponseEntity<List<ChatRoomListDto>> getMemberChatRooms(Principal principal) {
//        Long loggedInUserId = chatService.findByMemberId(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        Long loggedInUserId = 1L;
        log.debug("Logged In User ID: {}", loggedInUserId);
        //

        List<ChatRoomListDto> chatRooms = chatService.getMemberChatRooms(loggedInUserId);
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방에 있는 모든 메시지 가져오기 (오프라인 상태에서 재입장 시)
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<Flux<ChatMessageDto>> getChatRoomMessages(@PathVariable Long chatRoomId, Principal principal) {
//        Long loggedInUserId = chatService.findByMemberId(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        Long loggedInUserId = 1L;
        log.debug("Logged In User ID: {}", loggedInUserId);
        //

        if (!chatService.isChatRoomExist(chatRoomId)) {
            return ResponseEntity.notFound().build();
        }
        Flux<ChatMessageDto> messages = chatService.getChatRoomMessages(chatRoomId, loggedInUserId);

        return ResponseEntity.ok(messages);
    }

    // WebSocket을 통한 실시간 메시지 브로드캐스트 (온라인 상태 / 채팅방 단위)
    @MessageMapping("/send-message/{chatRoomId}")  // 채팅방에 해당하는 메시지 보내기
    @SendTo("/topic/{chatRoomId}")  // 채팅방에 입장 중인 유저에게 메시지 전달
    public ChatMessageDto sendMessageToChatRoom(@Payload ChatMessageDto chatMessageDto, @DestinationVariable Long chatRoomId) {
        if (chatRoomId == null ||chatRoomId <= 0) {
            log.error("Invalid chatRoomId: " + chatRoomId);
            throw new IllegalArgumentException("Invalid chatRoomId");
        }
        log.info("Sending message to chat room: " + chatRoomId);
        return chatService.sendMessageToKafka(chatMessageDto);
    }

//    @PutMapping("/{chatMessageId}")
//    public Mono<ResponseEntity<ChatMessageDto>> updateChatMessage(
//            @PathVariable String chatMessageId,
//            @RequestBody String newMessage) {
//        return chatService.updateChatMessage(chatMessageId, newMessage)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }

    @DeleteMapping("/{chatMessageId}")
    public Mono<ResponseEntity<Void>> deleteChatMessage(@PathVariable String chatMessageId) {
        return chatService.deleteChatMessage(chatMessageId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    // 채팅방 나가기 버튼을 눌렀을 때 유저 상태를 LEFT로 변경
    @PostMapping("/leave-room")
    public ResponseEntity<Void> leaveChatRoom(@RequestBody LeaveRoomRequest request) {
        if (request.getChatRoomId() == null || request.getMemberId() == null) {
            log.error("Invalid chat room ID or member ID");
            return ResponseEntity.badRequest().build();
        }
        chatService.leaveChatRoom(request.getChatRoomId(), request.getMemberId());
        return ResponseEntity.ok().build();
    }
}

