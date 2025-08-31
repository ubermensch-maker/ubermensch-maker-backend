package com.example.todo.admin;

import com.example.todo.admin.dto.PromptTemplateDto;
import com.example.todo.admin.dto.PromptTemplateListDto;
import com.example.todo.admin.dto.PromptTemplateUpdateDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PromptTemplateService {
  private final PromptTemplateRepository promptTemplateRepository;

  public PromptTemplateDto get(Long promptTemplateId) {
    PromptTemplate promptTemplate =
        promptTemplateRepository
            .findById(promptTemplateId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    return PromptTemplateDto.from(promptTemplate);
  }

  public PromptTemplateDto getByName(String name) {
    PromptTemplate promptTemplate =
        promptTemplateRepository
            .findByName(name)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    return PromptTemplateDto.from(promptTemplate);
  }

  public PromptTemplateListDto list() {
    List<PromptTemplate> promptTemplates = promptTemplateRepository.findAll();
    return new PromptTemplateListDto(
        promptTemplates.size(), promptTemplates.stream().map(PromptTemplateDto::from).toList());
  }

  public PromptTemplateDto update(Long promptTemplateId, PromptTemplateUpdateDto request) {
    PromptTemplate promptTemplate =
        promptTemplateRepository
            .findById(promptTemplateId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "System prompt not found"));

    promptTemplate.update(request.getName(), request.getContent(), request.getMetadata());

    return PromptTemplateDto.from(promptTemplate);
  }
}