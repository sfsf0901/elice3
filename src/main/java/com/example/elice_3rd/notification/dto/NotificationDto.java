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

    private String message;

    private Boolean readStatus;

    public static NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .chatRoomId(notification.getChatRoom().getChatRoomId())
                .receiverId(notification.getReceiver().getMemberId())
                .message(notification.getMessage())
                .readStatus(notification.isReadStatus())
                .build();
    }
}
