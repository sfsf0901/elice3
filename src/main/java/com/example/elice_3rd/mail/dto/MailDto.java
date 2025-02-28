package com.example.elice_3rd.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailDto {
    @Email
    @NotBlank
    private String address;
    @NotBlank
    private String content;
    @NotBlank
    private String code;
}
