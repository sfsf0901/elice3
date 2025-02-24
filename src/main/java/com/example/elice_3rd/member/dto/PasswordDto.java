package com.example.elice_3rd.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordDto {
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야합니다.")
    private String currentPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야합니다.")
    private String newPassword;
}
