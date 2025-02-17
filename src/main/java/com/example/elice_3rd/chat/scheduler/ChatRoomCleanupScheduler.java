package com.example.elice_3rd.chat.scheduler;

import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
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

    @Scheduled(cron = "0 0 4 ? * TUE")  // 매주 화요일 새벽 4시에 실행
    public void cleanupInactiveChatRooms() {
        // INACTIVE 상태인 채팅방들을 조회
        List<ChatRoom> inactiveChatRooms = chatRoomRepository.findByRoomStatus(RoomStatus.INACTIVE);

        for (ChatRoom chatRoom : inactiveChatRooms) {
            // 해당 채팅방의 메시지 삭제
            chatMessageRepository.deleteByChatRoomId(chatRoom.getChatRoomId());
            log.info("Messages in chat room {} have been deleted.", chatRoom.getChatRoomId());

            // 채팅방 삭제
            chatRoomRepository.delete(chatRoom);
            log.info("Chat room {} has been deleted.", chatRoom.getChatRoomId());
        }
    }
}
