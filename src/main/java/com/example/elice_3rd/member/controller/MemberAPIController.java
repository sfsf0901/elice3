package com.example.elice_3rd.member.controller;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/members")
@AllArgsConstructor
public class MemberAPIController {
    private final MemberService memberService;

    @PostMapping("register")
    public ResponseEntity<MemberResponseDto> register(@RequestBody @Validated MemberRequestDto requestDto){
        memberService.register(requestDto);
        return ResponseEntity.created(URI.create("/")).build();
    }

    @GetMapping("info")
    public ResponseEntity<MemberResponseDto> retrieve(@RequestBody String email){
        return ResponseEntity.ok(memberService.retrieveMember(email));
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
