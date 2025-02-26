package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.dto.*;
import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import com.example.elice_3rd.chat.entity.mysql.ChatReadStatus;
import com.example.elice_3rd.chat.entity.mysql.ChatRoom;
import com.example.elice_3rd.chat.entity.mysql.MemberStatus;
import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import com.example.elice_3rd.chat.repository.ChatReadStatusRepository;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.entity.Notification;
import com.example.elice_3rd.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final KafkaProducer kafkaProducer;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationRepository notificationRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;

    public Long findByMemberId (String memberId){
        Member member = memberRepository.findByEmail(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        return member.getMemberId();
    }

    // 기존 채팅방이 있는지 확인하고 채팅방 개설 혹은 연결
    @Transactional
    public ChatRoomResponseDto checkChatRoom(ChatRoomRequestDto request, Long loggedInUserId) {
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            throw new IllegalArgumentException("Member IDs cannot be null or empty");
        }

        // 요청이 1:1 채팅이라면 기존 채팅방을 확인 (그룹 채팅 기능 확장 가능하게 코드 구성)
        if (request.getMemberIds().size() == 2) {
            // 1:1 채팅방이 이미 존재하는지 확인
            List<ChatRoom> existingChatRooms = chatRoomRepository.findByMembers_MemberIdIn(request.getMemberIds());

            Optional<ChatRoom> exactMatchChatRoom = existingChatRooms.stream()
                    .filter(chatRoom -> {
                        Set<Long> memberIdsInRoom = chatRoom.getMembers().stream()
                                .map(member -> member.getMemberId())
                                .collect(Collectors.toSet());
                        return memberIdsInRoom.equals(new HashSet<>(request.getMemberIds()));
                    })
                    .findFirst();

            if (exactMatchChatRoom.isPresent()) {
                ChatRoom chatRoom = exactMatchChatRoom.get();

                // 채팅방 상태가 INACTIVE인 경우 새로운 채팅방 생성
                if (chatRoom.getRoomStatus() == RoomStatus.INACTIVE) {
                    return createChatRoom(request, loggedInUserId);
                }

                // 로그인한 사용자의 상태가 LEFT인 경우 - 상태를 ONLINE으로 변경하고, 채팅방으로 재연결
                boolean isUserLeft = chatRoom.getMemberStatuses().stream()
                        .anyMatch(memberStatus -> memberStatus.getMember().getMemberId().equals(loggedInUserId) &&
                                memberStatus.getStatus() == MemberStatusType.LEFT);
                if (isUserLeft) {
                    updateMemberStatusToOnline(chatRoom, loggedInUserId);
                    return ChatRoomResponseDto.toDto(chatRoom, loggedInUserId);
                }

                // 상태가 ACTIVE라면 해당 채팅방으로 연결
                return ChatRoomResponseDto.toDto(chatRoom, loggedInUserId);
            }
        }

        // 기존 채팅방이 없으면 새로운 채팅방 생성, 그룹 채팅인 경우에는 항상 새로운 채팅방 생성
        return createChatRoom(request, loggedInUserId);
    }

    private void updateMemberStatusToOnline(ChatRoom chatRoom, Long loggedInUserId) {
        MemberStatus memberStatus = chatRoom.getMemberStatuses().stream()
                .filter(status -> status.getMember().getMemberId().equals(loggedInUserId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

        memberStatus.setStatus(MemberStatusType.ONLINE);
        memberStatusRepository.save(memberStatus);

        log.info("Member {} status updated to ONLINE in chat room {}", loggedInUserId, chatRoom.getChatRoomId());
    }

    // 채팅방 개설 (새로운 채팅방 생성)
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto request, Long loggedInUserId) {
        // 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomStatus(RoomStatus.ACTIVE);
        chatRoom.setMembers(new HashSet<>());

        // 채팅방에 회원 추가
        Set<Member> members = new HashSet<>();
        for (Long memberId : request.getMemberIds()) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found"));
            members.add(member);
        }

        chatRoom.setMembers(members);
        chatRoom = chatRoomRepository.save(chatRoom);

        // 각 멤버의 상태 추가
        for (Member member : members) {
            MemberStatus memberStatus = new MemberStatus();
            memberStatus.setChatRoom(chatRoom);
            memberStatus.setMember(member);
            memberStatus.setStatus(MemberStatusType.ONLINE);
            memberStatusRepository.save(memberStatus);
        }

        log.info("New chat room created with ID: " + chatRoom.getChatRoomId());

        return ChatRoomResponseDto.toDto(chatRoom, loggedInUserId);
    }

    // 특정 멤버가 속한 모든 채팅방 목록 조회
    public List<ChatRoomListDto> getMemberChatRooms(Long memberId) {
        // MemberStatus를 조회하여 유저가 나가지 않은 채팅방들을 조회
        List<MemberStatus> memberStatuses = memberStatusRepository.findByMemberMemberIdAndStatusNot(memberId, MemberStatusType.LEFT);

        // 각 MemberStatus에서 ChatRoom만 추출하여 반환
        return memberStatuses.stream()
                .map(memberStatus -> {
                    ChatRoom chatRoom = memberStatus.getChatRoom();

                    // 가장 최근 메시지 조회
                    Mono<ChatMessage> lastMessageMono = chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId())
                    .sort(Comparator.comparing(ChatMessage::getCreatedDate).reversed())
                    .take(1)
                    .next();

                    // 안 읽은 메시지 수 계산
                    Mono<Long> unreadMessagesCountMono = chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId())
                            .filter(msg -> msg != null && msg.getSenderId() != null && !msg.getSenderId().equals(memberId))
                            .filter(msg -> {
                                Optional<ChatReadStatus> statusOpt = chatReadStatusRepository.findByChatMessageIdAndReceiver_memberId(msg.getChatMessageId(), memberId);
                                return statusOpt.map(status -> !status.isReadStatus()).orElse(true);
                            })
                            .switchIfEmpty(Flux.empty())
                            .count();

                    // 비동기 처리된 Mono를 block()으로 동기식으로 처리
                    ChatMessage lastMessage = lastMessageMono.block();
                    Long unreadMessagesCount = unreadMessagesCountMono.block();

                    unreadMessagesCount = (unreadMessagesCount != null) ? unreadMessagesCount : 0L;

                    String message = lastMessage != null ? lastMessage.getMessage() : null;

                    LocalDateTime lastModifiedDate = null;
                    if (lastMessage != null) {
                        if (lastMessage.getLastModifiedDate() != null) {
                            lastModifiedDate = lastMessage.getLastModifiedDate();
                        } else {
                            lastModifiedDate = lastMessage.getCreatedDate();
                        }
                    }

                    log.debug("채팅방 ID: " + chatRoom.getChatRoomId());
                    log.debug("가져온 메시지 : " + message);
                    log.debug("가져온 날짜: " + lastModifiedDate);
                    log.debug("가져온 안읽은 메세지 수: " + unreadMessagesCount);

                    return ChatRoomListDto.toDto(chatRoom, memberId, message, lastModifiedDate, unreadMessagesCount);
                })
                .collect(Collectors.toList());
    }

    public boolean isChatRoomExist(Long chatRoomId) {
        return chatRoomRepository.existsById(chatRoomId);
    }

    // 채팅방 메시지 가져오기
    public Flux<ChatMessageDto> getChatRoomMessages(Long chatRoomId, Long memberId) {
        // 채팅방과 멤버의 상태 정보를 가져옴
        MemberStatus memberStatus = memberStatusRepository.findByChatRoomChatRoomIdAndMemberMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

        // 멤버 상태가 오프라인 상태일 경우, 상태를 ONLINE으로 변경
        if (memberStatus.getStatus() == MemberStatusType.OFFLINE) {
            // 회원 상태를 ONLINE으로 업데이트
            memberStatus.setStatus(MemberStatusType.ONLINE);
            memberStatusRepository.save(memberStatus);

            log.info("Member {} has entered the chat room {} and status is updated to ONLINE", memberId, chatRoomId);
        }

        // 입장 시간을 기준으로 메시지 조회
        LocalDateTime messageFetchStartTime = memberStatus.getJoinedDate();

        // statusLeftChangedDate가 있는 유저는 그 이후 시간으로 조회
        if (memberStatus.getStatusLeftChangedDate() != null) {
            messageFetchStartTime = memberStatus.getStatusLeftChangedDate();
        }

        // 채팅방 메시지 조회와 동시에 읽음 상태를 업데이트
        Flux<ChatMessageDto> chatMessages = chatMessageRepository.findByChatRoomIdAndCreatedDateAfter(chatRoomId, messageFetchStartTime)
                .doOnNext(chatMessage -> {
                    // 상태가 ONLINE으로 변경된 시점 이전의 메시지들에 대해 읽음 상태 처리
                    if (memberStatus.getStatus() == MemberStatusType.ONLINE &&
                            !chatMessage.getCreatedDate().isAfter(memberStatus.getStatusOnlineChangedDate())) {
                        // 온라인 상태로 변경된 시점 이전에 발송된 메시지들을 읽음 처리
                        ChatReadStatus readStatus = chatReadStatusRepository.findByChatMessageIdAndReceiver_memberId(
                                        chatMessage.getChatMessageId(), memberId)
                                .orElseThrow(() -> new EntityNotFoundException("ChatReadStatus not found"));

                        readStatus.setReadStatus(true); // 메시지 읽음 처리
                        chatReadStatusRepository.save(readStatus);
                    }
                })
                .map(ChatMessageDto::toDto)
                .subscribeOn(Schedulers.boundedElastic());

        return chatMessages.hasElements()
                .flatMapMany(exists -> exists ? chatMessages : Flux.empty());
    }


    // 채팅방 상대 표시를 위해 멤버중 1명 추출
    public ChatRoomMemberDto findOtherMemberInChatRoom(Long chatRoomId, Long loggedInUserId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        // 채팅방에 속한 회원들 중 본인 제외 첫 번째 회원 찾기
        for (Member member : chatRoom.getMembers()) {
            if (!member.getMemberId().equals(loggedInUserId)) {
                return ChatRoomMemberDto.toDto(member);
            }
        }

        // 만약 본인이 유일한 멤버라면 예외 처리
        throw new IllegalStateException("No other members found in this chat room.");
    }

    // 채팅방 메시지 Kafka로 전송
    @Transactional
    public ChatMessageDto sendMessageToKafka(ChatMessageDto chatMessageDto) {
        try {
            // mongoDB에 저장
            ChatMessage chatMessage = chatMessageDto.toEntity();
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage).block();

            if (savedMessage == null) {
                log.error("Failed to save chat message. The saved message is null.");
                throw new RuntimeException("Failed to save chat message.");
            }

            log.debug("Saved chat message: {}", chatMessage);
            log.info("Chat message with ID {} Saved successfully.", chatMessage.getChatMessageId());

            processChatRoomAndStatus(savedMessage);

            // Kafka 메시지 전송, 실패 시 롤백
            kafkaProducer.sendMessage(chatMessage)
                    .exceptionally(e -> {
                        log.error("Failed to send message to Kafka: {}", chatMessage, e);
                        throw new RuntimeException("Transaction rollback due to Kafka message transmission failure.", e);
                    })
                    .join(); // 동기적으로 처리하여 예외 발생 시 트랜잭션 롤백

            return ChatMessageDto.toDto(savedMessage);

        } catch (Exception e) {
            log.error("Error processing chat message: {}", chatMessageDto.getMessage(), e);
            throw new RuntimeException("Error processing chat message", e);
        }
    }

    private void processChatRoomAndStatus(ChatMessage chatMessage) {
        // ChatRoom의 멤버들 조회 및 처리
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        // 각 멤버에 대해 ChatReadStatus 초기화
        for (Member receiver : chatRoom.getMembers()) {
            ChatReadStatus readStatus = new ChatReadStatus();
            readStatus.setChatMessageId(chatMessage.getChatMessageId());
            readStatus.setReceiver(receiver);
            chatReadStatusRepository.save(readStatus);

            // 수신자가 온라인 상태일 경우 즉시 읽음 처리
            MemberStatus memberStatus = memberStatusRepository.findByChatRoomChatRoomIdAndMemberMemberId(chatMessage.getChatRoomId(), receiver.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

            if (memberStatus.getStatus() == MemberStatusType.ONLINE) {
                readStatus.setReadStatus(true); // 온라인이면 읽음 상태로 처리
                chatReadStatusRepository.save(readStatus);
            }
        }
    }

    // 채팅방 메시지 수정
//    public Mono<ChatMessageDto> updateChatMessage(String chatMessageId, String newMessage) {
//        return chatMessageRepository.findById(chatMessageId)
//                .switchIfEmpty(Mono.error(new EntityNotFoundException("ChatMessage not found")))
//                .flatMap(chatMessage -> {
//                    chatMessage.setMessage(newMessage);
//                    return chatMessageRepository.save(chatMessage)
//                            .doOnSuccess(updatedMessage ->
//                                    log.info("Chat message with ID {} updated successfully.", chatMessageId)
//                            );
//                })
//                .map(ChatMessageDto::toDto);
//    }

    // 채팅방 메시지 삭제
    public Mono<Void> deleteChatMessage(String chatMessageId) {
        // 메세지 읽음 상태 삭제
        List<ChatReadStatus> chatReadStatuses = chatReadStatusRepository.findByChatMessageId(chatMessageId);
        for (ChatReadStatus chatReadStatus : chatReadStatuses) {
            chatReadStatusRepository.delete(chatReadStatus);
        }

        List<Notification> notifications = notificationRepository.findByChatMessageId(chatMessageId);
        // 알림 업데이트
        notifications.forEach(notification -> {
            notification.setChatMessageId(null);
            notification.setMessage("삭제된 메시지입니다.");
            notificationRepository.save(notification);
        });

        // 메시지 조회 및 삭제
        return chatMessageRepository.findById(chatMessageId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("ChatMessage not found")))
                .flatMap(chatMessageRepository::delete)
                .doOnSuccess(v -> log.info("Chat message with ID {} deleted successfully.", chatMessageId));
    }


    // 채팅방 나가기 처리
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        // 이용자 상태를 LEFT로 변경
        MemberStatus memberStatus = memberStatusRepository.findByChatRoomChatRoomIdAndMemberMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

        memberStatus.setStatus(MemberStatusType.LEFT);
        memberStatusRepository.save(memberStatus);

        log.info("Member {} has left the chat room {}", memberId, chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        // 채팅방 상태 점검
        if(chatRoom.checkAndCloseRoomIfAllMembersLeft()) {
            // 상태가 변경된 경우 저장
            chatRoomRepository.save(chatRoom);
            log.info("Chat room {} status set to INACTIVE, as all members have left.", chatRoomId);
        }
    }

    // WebSocket 연결이 끊어졌을 때 호출
    public void updateMemberStatusToOffline(Long memberId) {
        MemberStatus memberStatus = memberStatusRepository.findOneByMemberMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

        // 상태를 OFFLINE으로 변경
        memberStatus.setStatus(MemberStatusType.OFFLINE);
        memberStatusRepository.save(memberStatus);

        log.info("Member {} status updated to OFFLINE due to WebSocket disconnection.", memberId);
    }
}
