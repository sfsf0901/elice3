package com.example.elice_3rd.chat.controller;

import com.example.elice_3rd.chat.dto.ChatRoomMemberDto;
import com.example.elice_3rd.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
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
    public String getChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId, Model model, Principal principal) {
        if (!chatService.isChatRoomExist(chatRoomId)) {
            log.error("Chat room with ID {} not found", chatRoomId);
            return "redirect:/";
        }
        try {
            if (principal == null) {
                log.warn("User ID is null for principal {}", principal.getName());
                throw new InsufficientAuthenticationException("Member is not authenticated");
            }
            String loggedInUserName = chatService.getMemberName(principal.getName());
            log.debug("Logged In User Name: {}", loggedInUserName);
            model.addAttribute("memberName", loggedInUserName);

            ChatRoomMemberDto chatRoomMemberDto = chatService.findOtherMemberInChatRoom(chatRoomId, memberId);
            model.addAttribute("chatRoomMemberDto", chatRoomMemberDto);
        } catch (Exception e) {
            log.error("Error retrieving chat room member information for chat room ID {}: {}", chatRoomId, e.getMessage());
        }
        return "chat/chat-room";
    }

    @GetMapping("/chat-rooms")
    public String getChatRoomsPage(Model model, Principal principal) {
        try {
            if (principal == null) {
                log.warn("User ID is null for principal {}", principal.getName());
                throw new InsufficientAuthenticationException("Member is not authenticated");
            }
            Long loggedInUserId = chatService.findByMemberId(principal.getName());
            log.debug("Logged In User ID: {}", loggedInUserId);

            if (loggedInUserId == null) {
                return "redirect:/member/login";
            }
            model.addAttribute("memberId", loggedInUserId);
        } catch (Exception e) {
            log.error("Error retrieving user ID for principal {}: {}", principal.getName(), e.getMessage());
        }

        return "chat/chat-room-list";
    }
}
