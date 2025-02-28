package com.example.elice_3rd.member;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberControllerTest {

    @Autowired
    MemberService memberService;

    // @Test
    // @DisplayName("회원가입 테스트")
    // void register() {
    //     MemberRequestDto requestDto = MemberRequestDto.builder()
    //             .email("test")
    //             .name("test name")
    //             .password("test")
    //             .build();
    //     memberService.register(requestDto);
    // }

    // @Test
    // @DisplayName("비밀번호 변경 테스트")
    // void updatePassword() {
    //     String password = "new password";

    //     memberService.updatePassword("test email", password);
    // }

    // @Test
    // @DisplayName("회원 정보 수정 테스트")
    // void updateInfo() {
    //     String email = "test email";
    //     MemberUpdateDto updateDto = MemberUpdateDto.builder()
    //             .name("new name")
    //             .build();

    //     memberService.updateMemberInfo(email, updateDto);
    // }

}
