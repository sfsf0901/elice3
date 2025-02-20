package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // TODO : 이후에 상담 상세 페이지로 해당 버튼 이전 필요
    @GetMapping("/check-chat-room")
    public String getChatRoomBtnPage() {
        return "chat/create-chat-room";
    }

    @GetMapping("/chat-room/{chatRoomId}/{memberId}")
    public String getChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId) {
        if (!chatService.isChatRoomExist(chatRoomId)) {
            return "redirect:/";
        }
        return "chat/chat-room";
    }

    @GetMapping("/chat-rooms")
    public String getChatRoomsPage(Model model, Principal principal) {
//        Long loggedInUserId = Long.parseLong(principal.getName());
//        log.debug("Logged In User ID: {}", loggedInUserId);

        // 테스트용
        Long loggedInUserId = 1L;
        log.debug("Logged In User ID: {}", loggedInUserId);
        //
        if (loggedInUserId == null) {
            // 로그인되지 않은 상태라면 로그인 페이지로 리다이렉트
            return "redirect:/member/another-login";
        }
        model.addAttribute("memberId", loggedInUserId);

        return "chat/chat-room-list";
    }
}
