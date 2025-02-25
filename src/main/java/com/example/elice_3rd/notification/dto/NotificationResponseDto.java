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
public class NotificationResponseDto {

    private Long notificationId;

    private Long chatRoomId;

    private Long receiverId;

    private String chatMessageId;

    private String message;

    private Boolean readStatus;

    public static NotificationResponseDto toDto(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .chatRoomId(notification.getChatRoomId().getChatRoomId())
                .receiverId(notification.getReceiverId().getMemberId())
                .message(notification.getMessage())
                .readStatus(notification.isReadStatus())
                .build();
    }
}
