package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.dto.ChatMessageDto;
import com.example.elice_3rd.chat.dto.ChatRoomDto;
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
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public ChatRoomResponseDto checkChatRoom(ChatRoomRequestDto request) {
        // 요청이 1:1 채팅이라면 기존 채팅방을 확인 (그룹 채팅 기능 확장 가능하게 코드 구성)
        if (request.getMemberIds().size() == 2) {
            // 1:1 채팅방이 이미 존재하는지 확인
            Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByMembers_MemberIdIn(request.getMemberIds());

            if (existingChatRoom.isPresent()) {
                // 기존 1:1 채팅방이 있으면 해당 채팅방으로 연결
                return ChatRoomResponseDto.toDto(existingChatRoom.get());
            }
        }

        // 기존 채팅방이 없으면 새로운 채팅방 생성, 그룹 채팅인 경우에는 항상 새로운 채팅방 생성
        return createChatRoom(request);
    }

    // 채팅방 개설 (새로운 채팅방 생성)
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto request) {
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

        return ChatRoomResponseDto.toDto(chatRoom);
    }

    public List<ChatRoomDto> getMemberChatRooms(Long memberId) {
        // 특정 멤버가 속한 모든 채팅방 목록 조회
        // MemberStatus를 조회하여 유저가 속한 채팅방들을 가져옴
        List<MemberStatus> memberStatuses = memberStatusRepository.findByMemberMemberId(memberId);

        // 각 MemberStatus에서 ChatRoom만 추출하여 반환
        return memberStatuses.stream()
                .map(memberStatus -> ChatRoomDto.toDto(memberStatus.getChatRoom(), memberId))
                .collect(Collectors.toList());
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
        LocalDateTime joinTime = memberStatus.getJoinedDate();

        // 채팅방 메시지 조회와 동시에 읽음 상태를 업데이트
        return chatMessageRepository.findByChatRoomIdAndCreatedDateAfter(chatRoomId, joinTime)
                .doOnNext(chatMessage -> {
                    // 상태가 ONLINE으로 변경된 시점 이전의 메시지들에 대해 읽음 상태 처리
                    if (memberStatus.getStatus() == MemberStatusType.ONLINE &&
                            !chatMessage.getCreatedDate().isAfter(memberStatus.getStatusChangedDate())) {
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
    }


    // 채팅방 메시지 Kafka로 전송
    @Transactional
    public void sendMessageToKafka(ChatMessageDto chatMessageDto) {
        // mongoDB에 저장
        ChatMessage chatMessage = chatMessageDto.toEntity();
        chatMessageRepository.save(chatMessage);

        // ChatRoom의 멤버들 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        // 각 멤버에 대해 ChatReadStatus 초기화
        for (Member receiver : chatRoom.getMembers()) {
            ChatReadStatus readStatus = new ChatReadStatus();
            readStatus.setChatMessageId(chatMessage.getChatMessageId());
            readStatus.setReceiver(receiver);
            readStatus.setReadStatus(false); // 기본값: 읽지 않음
            chatReadStatusRepository.save(readStatus);

            // 수신자가 온라인 상태일 경우 즉시 읽음 처리
            MemberStatus memberStatus = memberStatusRepository.findByChatRoomChatRoomIdAndMemberMemberId(chatMessage.getChatRoomId(), receiver.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("MemberStatus not found"));

            if (memberStatus.getStatus() == MemberStatusType.ONLINE) {
                readStatus.setReadStatus(true); // 온라인이면 읽음 상태로 처리
                chatReadStatusRepository.save(readStatus);
            }
        }

        // Kafka로 메시지 전송
        kafkaProducer.sendMessage(chatMessage);
        log.info("Message sent to Kafka: " + chatMessage);
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
