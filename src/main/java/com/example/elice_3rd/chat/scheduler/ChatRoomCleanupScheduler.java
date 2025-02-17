package com.example.elice_3rd.chat.scheduler;

import com.example.elice_3rd.chat.entity.ChatMessage;
import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import com.example.elice_3rd.chat.repository.ChatReadStatusRepository;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCleanupScheduler {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;

    @Scheduled(cron = "${scheduler.cleanup.cron}") // 매주 화요일 새벽 4시에 실행
    public void cleanupInactiveChatRooms() {
        // INACTIVE 상태인 채팅방들을 조회
        List<ChatRoom> inactiveChatRooms = chatRoomRepository.findByRoomStatus(RoomStatus.INACTIVE);

        for (ChatRoom chatRoom : inactiveChatRooms) {
            // 해당 채팅방에 속한 메시지들을 chatRoomId를 통해 찾음
            List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId());

            for (ChatMessage chatMessage : chatMessages) {
                // 해당 채팅방의 메시지 읽은 상태 삭제
                chatReadStatusRepository.deleteByChatMessageId(chatMessage.getChatMessageId());
                log.debug("Chat read status for message {} has been deleted.", chatMessage.getChatMessageId());
            }

            // 해당 채팅방의 메시지 삭제
            chatMessageRepository.deleteByChatRoomId(chatRoom.getChatRoomId());
            log.debug("Messages in chat room {} have been deleted.", chatRoom.getChatRoomId());

            // 해당 채팅방의 멤버 상태 삭제
            memberStatusRepository.deleteByChatRoom_ChatRoomId(chatRoom.getChatRoomId());
            log.debug("Member statuses in chat room {} have been deleted.", chatRoom.getChatRoomId());

            // 채팅방 삭제
            chatRoomRepository.delete(chatRoom);
            log.debug("Chat room {} has been deleted.", chatRoom.getChatRoomId());
        }
    }
}
