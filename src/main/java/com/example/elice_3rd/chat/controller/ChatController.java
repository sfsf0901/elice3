package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.dto.ChatRoomMemberDto;
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

    @GetMapping("/chat-room/{chatRoomId}/{memberId}")
    public String getChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId, Model model) {
        if (!chatService.isChatRoomExist(chatRoomId)) {
            return "redirect:/";
        }
        ChatRoomMemberDto chatRoomMemberDto = chatService.findOtherMemberInChatRoom(chatRoomId, memberId);
        model.addAttribute("chatRoomMemberDto", chatRoomMemberDto);

        return "chat/chat-room";
    }

    @GetMapping("/chat-rooms")
    public String getChatRoomsPage(Model model, Principal principal) {
        Long loggedInUserId = chatService.findByMemberId(principal.getName());
        log.debug("Logged In User ID: {}", loggedInUserId);

        if (loggedInUserId == null) {
            return "redirect:/member/another-login";
        }
        model.addAttribute("memberId", loggedInUserId);

        return "chat/chat-room-list";
    }
}
