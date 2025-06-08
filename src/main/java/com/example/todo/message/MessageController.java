package com.example.todo.message;

import com.example.todo.message.dto.MessageCreateDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.MessageListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/messages")
    public MessageDto create(@RequestParam Long userId, @RequestBody MessageCreateDto request) {
        return messageService.create(userId, request);
    }

    @GetMapping("/messages/{messageId}")
    public MessageDto read(@PathVariable Long messageId) {
        return messageService.read(messageId);
    }

    @GetMapping("/messages")
    public MessageListDto list(@RequestParam Long userId, @RequestParam Long conversationId) {
        return messageService.list(userId, conversationId);
    }

    @DeleteMapping("/messages/{messageId}")
    public void delete(@RequestParam Long userId, @PathVariable Long messageId) {
        messageService.delete(userId, messageId);
    }
}
