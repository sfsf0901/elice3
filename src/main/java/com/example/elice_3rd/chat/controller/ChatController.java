package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.dto.*;
import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 연결
    @PostMapping("/check-chat-room")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@RequestBody ChatRoomRequestDto request) {
        ChatRoomResponseDto chatRoomDto = chatService.checkChatRoom(request);
        return ResponseEntity.ok(chatRoomDto);
    }

    // 특정 멤버가 속한 모든 채팅방 목록 조회
    @GetMapping("/chat-rooms/{memberId}")
    public ResponseEntity<List<ChatRoomDto>> getMemberChatRooms(@PathVariable Long memberId) {
        List<ChatRoomDto> chatRooms = chatService.getMemberChatRooms(memberId);
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방에 있는 모든 메시지 가져오기 (오프라인 상태에서 재입장 시) - SSE (Server-Sent Events) 방식
    @GetMapping(value = "/{chatRoomId}/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ChatMessageDto>> getChatRoomMessages(@PathVariable Long chatRoomId, @PathVariable Long memberId) {
        Flux<ChatMessageDto> messages = chatService.getChatRoomMessages(chatRoomId, memberId);
        return ResponseEntity.ok(messages);
    }

    // WebSocket을 통한 실시간 메시지 브로드캐스트 (온라인 상태 / 채팅방 단위)
    @MessageMapping("/send-message/{chatRoomId}")  // 채팅방에 해당하는 메시지 보내기
    @SendTo("/topic/{chatRoomId}")  // 채팅방에 입장 중인 유저에게 메시지 전달
    public ChatMessageDto sendMessageToChatRoom(@Payload ChatMessageDto chatMessageDto, @DestinationVariable Long chatRoomId) {
        log.info("Sending message to chat room: " + chatRoomId);
        chatService.sendMessageToKafka(chatMessageDto);
        return chatMessageDto;
    }

    // 채팅방 나가기 버튼을 눌렀을 때 유저 상태를 LEFT로 변경
    @PostMapping("/leave-room")
    public ResponseEntity<Void> leaveChatRoom(@RequestBody LeaveRoomRequest request) {
        chatService.leaveChatRoom(request.getChatRoomId(), request.getMemberId());
        return ResponseEntity.ok().build();
    }

//    // 나가기 버튼 클릭 시 js 참고
//document.getElementById("leaveButton").addEventListener("click", function() {
//        fetch('/api/chat/leaveRoom', {
//                method: 'POST',
//                headers: {
//            'Content-Type': 'application/json',
//        },
//        body: JSON.stringify({
//                chatRoomId: chatRoomId,  // 채팅방 ID
//                memberId: memberId      // 유저 ID
//        })
//    })
//    .then(response => response.json())
//    .then(data => {
//                console.log("User has left the chat room, status set to LEFT");
//    })
//    .catch(error => {
//                console.error("Error leaving the chat room", error);
//    });
//    });

}

