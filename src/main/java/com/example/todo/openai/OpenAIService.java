package com.example.todo.openai;

import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.enums.Model;
import com.example.todo.toolcall.functions.CreateGoal;
import com.example.todo.toolcall.functions.CreateMilestone;
import com.example.todo.toolcall.functions.CreateQuest;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIService {
  private final OpenAIClient client;

  public String chatCompletion(List<MessageDto> messages, Model model) {
    try {
      ChatCompletionCreateParams.Builder paramsBuilder =
          ChatCompletionCreateParams.builder().model(model.getValue());

      for (MessageDto message : messages) {
        String role = message.getRole().toString().toLowerCase();
        List<ContentDto> contents = message.getContent();

        for (ContentDto content : contents) {
          if (content instanceof TextContentDto) {
            TextContentDto textContent = (TextContentDto) content;
            String text = textContent.getText();

            if ("user".equals(role)) {
              paramsBuilder.addUserMessage(text);
            } else if ("assistant".equals(role)) {
              paramsBuilder.addAssistantMessage(text);
            } else if ("system".equals(role)) {
              paramsBuilder.addSystemMessage(text);
            }
          }
        }
      }

      ChatCompletionCreateParams params = paramsBuilder.build();
      ChatCompletion chatCompletion = client.chat().completions().create(params);

      return chatCompletion.choices().get(0).message().content().orElse("");
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI chat completion", e);
    }
  }

  public ChatCompletion chatCompletionWithTools(List<MessageDto> messages, Model model) {
    try {
      ChatCompletionCreateParams.Builder paramsBuilder =
          ChatCompletionCreateParams.builder()
              .model(model.getValue())
              .addTool(CreateGoal.class)
              .addTool(CreateMilestone.class)
              .addTool(CreateQuest.class);

      for (MessageDto message : messages) {
        String role = message.getRole().toString().toLowerCase();
        List<ContentDto> contents = message.getContent();

        for (ContentDto content : contents) {
          if (content instanceof TextContentDto) {
            TextContentDto textContent = (TextContentDto) content;
            String text = textContent.getText();

            if ("user".equals(role)) {
              paramsBuilder.addUserMessage(text);
            } else if ("assistant".equals(role)) {
              paramsBuilder.addAssistantMessage(text);
            } else if ("system".equals(role)) {
              paramsBuilder.addSystemMessage(text);
            }
          }
        }
      }

      ChatCompletionCreateParams params = paramsBuilder.build();
      return client.chat().completions().create(params);
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI chat completion with tools", e);
    }
  }
}
