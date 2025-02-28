package com.example.elice_3rd.chat.entity.mysql;

import com.example.elice_3rd.chat.entity.status.MemberStatusType;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(nullable = false)
    private RoomStatus roomStatus = RoomStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chat_room_members",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<Member> members;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private Set<MemberStatus> memberStatuses;

    public boolean checkAndCloseRoomIfAllMembersLeft() {
        boolean allMembersLeft = memberStatuses.stream()
                .allMatch(status -> status.getStatus() == MemberStatusType.LEFT);

        if (allMembersLeft) {
            this.roomStatus = RoomStatus.INACTIVE;
            return true;
        }
        return false;
    }
}