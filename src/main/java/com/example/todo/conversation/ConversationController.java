package com.example.todo.conversation;

import com.example.todo.conversation.dto.ConversationCreateDto;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.conversation.dto.ConversationListDto;
import com.example.todo.conversation.dto.ConversationUpdateDto;
import com.example.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    private final UserService userService;

    @PostMapping("/conversations")
    public ConversationDto create(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody ConversationCreateDto request
    ) {
        Long userId = userService.getByEmail(principal.getUsername()).getId();
        return conversationService.create(userId, request);
    }

    @GetMapping("/conversations/{conversationId}")
    public ConversationDto read(@PathVariable Long conversationId) {
        return conversationService.read(conversationId);
    }

    @GetMapping("/conversations")
    public ConversationListDto list(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestParam(required = false) Long goalId
    ) {
        Long userId = userService.getByEmail(principal.getUsername()).getId();
        return conversationService.list(userId, goalId);
    }

    @PutMapping("/conversations/{conversationId}")
    public ConversationDto update(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PathVariable Long conversationId,
            @RequestBody ConversationUpdateDto request
    ) {
        Long userId = userService.getByEmail(principal.getUsername()).getId();
        return conversationService.update(userId, conversationId, request);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public void delete(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PathVariable Long conversationId
    ) {
        Long userId = userService.getByEmail(principal.getUsername()).getId();
        conversationService.delete(userId, conversationId);
    }
}