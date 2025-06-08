package com.example.todo.conversation;

import com.example.todo.conversation.dto.ConversationCreateDto;
import com.example.todo.conversation.dto.ConversationDto;
import com.example.todo.conversation.dto.ConversationUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/conversations")
    public ConversationDto create(@RequestParam Long userId, @RequestBody ConversationCreateDto request) {
        return conversationService.create(userId, request);
    }

    @GetMapping("/conversations/{conversationId}")
    public ConversationDto read(@PathVariable Long conversationId) {
        return conversationService.read(conversationId);
    }

    @PutMapping("/conversations/{conversationId}")
    public ConversationDto update(@RequestParam Long userId, @PathVariable Long conversationId, @RequestBody ConversationUpdateDto request) {
        return conversationService.update(userId, conversationId, request);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public void delete(@RequestParam Long userId, @PathVariable Long conversationId) {
        conversationService.delete(userId, conversationId);
    }
}