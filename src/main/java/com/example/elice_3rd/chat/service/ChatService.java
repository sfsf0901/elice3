package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.dto.ChatMessageDto;
import com.example.elice_3rd.chat.dto.ChatRoomListDto;
import com.example.elice_3rd.chat.dto.ChatRoomResponseDto;
import com.example.elice_3rd.chat.dto.ChatRoomRequestDto;
import com.example.elice_3rd.chat.entity.ChatMessage;
import com.example.elice_3rd.chat.entity.ChatReadStatus;
import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.MemberStatus;
import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import com.example.elice_3rd.chat.repository.ChatReadStatusRepository;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
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

    /*구현할 기능
    1. 채팅방 이용자 상태 관리 입장 / 퇴장 / 오프라인 O
    2. 채팅방 이용자가 모두 퇴장시 채팅 방 상태 활성 / 비활성 O
    3. 채팅방 상태 비활성시에 메세지 전체 삭제 및 채팅방 삭제 처리 (스케쥴러로 관리) O
    4. 채팅방 메세지 작성 O
    5. 채팅방 메세지 수정
    6. 채팅방 메세지 삭제 (상태 변경X -> 삭제 처리)
    7. 채팅방 메세지 읽음 상태 변경 O
    8. 채팅방 목록 O
    9. 채팅방 내 메세지 리스트 O
    */

    private final KafkaProducer kafkaProducer;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;

    // 기존 채팅방이 있는지 확인하고 채팅방 개설 혹은 연결
    @Transactional
    public ChatRoomResponseDto checkChatRoom(ChatRoomRequestDto request, Long loggedInUserId) {
        if (request.getMemberIds() == null || request.getMemberIds().isEmpty()) {
            throw new IllegalArgumentException("Member IDs cannot be null or empty");
        }

        // 요청이 1:1 채팅이라면 기존 채팅방을 확인 (그룹 채팅 기능 확장 가능하게 코드 구성)
        if (request.getMemberIds().size() == 2) {
            // 1:1 채팅방이 이미 존재하는지 확인
            Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByMembers_MemberIdIn(request.getMemberIds());

            if (existingChatRoom.isPresent()) {
                ChatRoom chatRoom = existingChatRoom.get();

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

        log.info("User {} status updated to ONLINE in chat room {}", loggedInUserId, chatRoom.getChatRoomId());
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

    public List<ChatRoomListDto> getMemberChatRooms(Long memberId) {
        // 특정 멤버가 속한 모든 채팅방 목록 조회
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
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        return chatRoom.isPresent();  // 존재하면 true, 없으면 false 반환
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

            log.info("User {} has entered the chat room {} and status is updated to ONLINE", memberId, chatRoomId);
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

    // 채팅방 메시지 Kafka로 전송
    @Transactional
    public void sendMessageToKafka(ChatMessageDto chatMessageDto) {
        try {
            // mongoDB에 저장
            ChatMessage chatMessage = chatMessageDto.toEntity();
            chatMessageRepository.save(chatMessage);
            log.info("Saved chat message: {}", chatMessage);

            // ChatRoom의 멤버들 조회
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

            // Kafka 메시지 전송, 실패 시 롤백
            kafkaProducer.sendMessage(chatMessage)
                    .exceptionally(e -> {
                        log.error("Failed to send message to Kafka: {}", chatMessage, e);
                        throw new RuntimeException("Transaction rollback due to Kafka message transmission failure.", e);
                    })
                    .join(); // 동기적으로 처리하여 예외 발생 시 트랜잭션 롤백

        } catch (Exception e) {
            log.error("Error processing chat message: {}", chatMessageDto.getMessage(), e);
            throw new RuntimeException("Error processing chat message", e);
        }
    }

    // 채팅방 메시지 수정
    public Mono<ChatMessageDto> updateChatMessage(String chatMessageId, String newMessage) {
        return chatMessageRepository.findById(chatMessageId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("ChatMessage not found")))
                .flatMap(chatMessage -> {
                    chatMessage.setMessage(newMessage);
                    return chatMessageRepository.save(chatMessage)
                            .doOnSuccess(updatedMessage ->
                                    log.info("Chat message with ID {} updated successfully.", chatMessageId)
                            );
                })
                .map(ChatMessageDto::toDto);
    }

    // 채팅방 메시지 삭제
    public Mono<Void> deleteChatMessage(String chatMessageId) {
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

        log.info("User {} has left the chat room {}", memberId, chatRoomId);

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

        log.info("User {} status updated to OFFLINE due to WebSocket disconnection.", memberId);
    }
}
