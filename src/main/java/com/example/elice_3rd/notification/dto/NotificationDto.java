package com.example.elice_3rd.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long chatRoom;

    private Long sender;

    private String message;

    private Boolean readStatus;
}
