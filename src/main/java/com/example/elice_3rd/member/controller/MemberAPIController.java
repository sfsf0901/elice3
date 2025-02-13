package com.example.elice_3rd.member.controller;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.service.MemberService;
import com.example.elice_3rd.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberAPIController {
    private final MemberService memberService;
    // component를 주입 시키는 것에는 정답은 없음
    private final JwtUtil jwtUtil;

    @PostMapping("register")
    public ResponseEntity<MemberResponseDto> register(@RequestBody @Validated MemberRequestDto requestDto){
        memberService.register(requestDto);
        return ResponseEntity.created(URI.create("/")).build();
    }

    @GetMapping("info")
    public ResponseEntity<MemberResponseDto> retrieve(Principal principal){
        return ResponseEntity.ok(memberService.retrieveMember(principal.getName()));
    }

    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(Principal principal, @RequestBody String password){
        memberService.updatePassword(principal.getName(), password);
        return ResponseEntity.ok().header("Location", "my-page").build();
    }

    @PutMapping("info")
    public ResponseEntity<Void> updateMemberInfo(Principal principal, @RequestBody MemberUpdateDto updateDto){
        memberService.updateMemberInfo(principal.getName(), updateDto);
        return ResponseEntity.ok().header("Location", "my-page").build();
    }

    @PutMapping("quit")
    public ResponseEntity<Void> quit(Principal principal){
        memberService.quit(principal.getName());
        return ResponseEntity.ok().header("Location", "/").build();
    }
}
