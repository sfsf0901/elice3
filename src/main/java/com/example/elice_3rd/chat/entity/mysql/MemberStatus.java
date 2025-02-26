package com.example.elice_3rd.chat.entity.mysql;

import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
    @Column(updatable = false)
    private LocalDateTime joinedDate; // 채팅방 최초 입장 시간

    private LocalDateTime statusOnlineChangedDate; // 멤버 상태(ONLINE) 변경 시간

    private LocalDateTime statusLeftChangedDate; // 멤버 상태(LEFT) 변경 시간

    public void setStatus(MemberStatusType status) {
        if (this.status != status) {
            this.status = status;

            // 상태 ONLINE일 때만 statusOnlineChangedDate 업데이트
            if (status == MemberStatusType.ONLINE) {
                this.statusOnlineChangedDate = LocalDateTime.now();
            }

            // 상태 LEFT 때만 statusLeftChangedDate 업데이트
            if (status == MemberStatusType.LEFT) {
                this.statusLeftChangedDate = LocalDateTime.now();
            }
        }
    }
}