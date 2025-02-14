package com.example.elice_3rd.chat.entity;

import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Builder.Default
    private MemberStatusType status = MemberStatusType.ONLINE;

    @CreatedDate
    private LocalDateTime joinedDate; // 채팅방 최초 입장 시간

    private LocalDateTime statusChangedDate; // 채팅방 상태(ONLINE) 변경 시간

    public void setStatus(MemberStatusType status) {
        // 상태 ONLINE일 때만 statusChangedDate를 업데이트
        if (this.status != status) {
            this.status = status;

            if (status == MemberStatusType.ONLINE) {
                this.statusChangedDate = LocalDateTime.now();
            }
        }
    }
}