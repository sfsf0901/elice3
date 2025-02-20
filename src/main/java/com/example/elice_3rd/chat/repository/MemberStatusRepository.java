package com.example.elice_3rd.chat.repository;


import com.example.elice_3rd.chat.entity.MemberStatus;
import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberStatusRepository extends JpaRepository<MemberStatus, Long> {

    Optional<MemberStatus> findOneByMemberMemberId(Long memberId);

    List<MemberStatus> findByMemberMemberIdAndStatusNot(Long memberId, MemberStatusType status);

    Optional<MemberStatus> findByChatRoomChatRoomIdAndMemberMemberId(Long chatRoomId, Long memberId);

    void deleteByChatRoom_ChatRoomId(Long chatRoomId);
}
