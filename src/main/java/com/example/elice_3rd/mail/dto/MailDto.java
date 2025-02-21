package com.example.elice_3rd.mail.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailDto {
    private String address;
    private String content;
    private String code;
}
