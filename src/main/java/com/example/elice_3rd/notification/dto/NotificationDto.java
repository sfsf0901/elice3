package com.example.elice_3rd.notification.dto;

import com.example.elice_3rd.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long chatRoomId;

    private Long receiverId;

    private String chatMessageId;

    private String content;

    private Boolean readStatus;

    public static NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .chatRoomId(notification.getChatRoomId().getChatRoomId())
                .receiverId(notification.getReceiverId().getMemberId())
                .content(notification.getMessage())
                .readStatus(notification.isReadStatus())
                .build();
    }
}
