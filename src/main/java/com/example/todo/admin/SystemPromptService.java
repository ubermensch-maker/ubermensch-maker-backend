package com.example.todo.admin;

import com.example.todo.admin.dto.SystemPromptDto;
import com.example.todo.admin.dto.SystemPromptListDto;
import com.example.todo.admin.dto.SystemPromptUpdateDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SystemPromptService {
  private final SystemPromptRepository systemPromptRepository;

  public SystemPromptDto get(Long systemPromptId) {
    SystemPrompt systemPrompt =
        systemPromptRepository
            .findById(systemPromptId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    return SystemPromptDto.from(systemPrompt);
  }

  public SystemPromptDto getByName(String name) {
    SystemPrompt systemPrompt =
        systemPromptRepository
            .findByName(name)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    return SystemPromptDto.from(systemPrompt);
  }

  public SystemPromptListDto list() {
    List<SystemPrompt> systemPrompts = systemPromptRepository.findAll();
    return new SystemPromptListDto(
        systemPrompts.size(), systemPrompts.stream().map(SystemPromptDto::from).toList());
  }

  public SystemPromptDto update(Long systemPromptId, SystemPromptUpdateDto request) {
    SystemPrompt systemPrompt =
        systemPromptRepository
            .findById(systemPromptId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    systemPrompt.update(request.getName(), request.getPrompt(), request.getMetadata());

    return SystemPromptDto.from(systemPrompt);
  }
}