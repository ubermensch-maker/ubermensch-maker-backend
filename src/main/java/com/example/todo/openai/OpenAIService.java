package com.example.todo.openai;

import com.example.todo.admin.PromptTemplateService;
import com.example.todo.message.Message;
import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.TextContentDto;
import com.example.todo.message.enums.Model;
import com.example.todo.toolcall.functions.CreateGoal;
import com.example.todo.toolcall.functions.CreateMilestone;
import com.example.todo.toolcall.functions.CreateQuest;
import com.example.todo.usage.TokenUsage;
import com.example.todo.usage.TokenUsageRepository;
import com.example.todo.user.User;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIService {
  private final OpenAIClient client;
  private final TokenUsageRepository tokenUsageRepository;
  private final PromptTemplateService promptTemplateService;

  public String chatCompletion(List<MessageDto> messages, Model model, User user, Message message) {
    try {
      ChatCompletionCreateParams.Builder paramsBuilder =
          ChatCompletionCreateParams.builder().model(model.getValue());

      for (MessageDto msg : messages) {
        String role = msg.getRole().toString().toLowerCase();
        List<ContentDto> contents = msg.getContent();

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

      // Token usage 저장
      if (chatCompletion.usage().isPresent()) {
        saveTokenUsage(user, message, model, chatCompletion.usage().get(), "chat_completion");
      }

      return chatCompletion.choices().get(0).message().content().orElse("");
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI chat completion", e);
    }
  }

  // 기존 호환성을 위한 오버로드 메서드
  public String chatCompletion(List<MessageDto> messages, Model model) {
    return chatCompletion(messages, model, null, null);
  }

  public ChatCompletion chatCompletionWithTools(
      List<MessageDto> messages, Model model, User user, Message message) {
    try {
      ChatCompletionCreateParams.Builder paramsBuilder =
          ChatCompletionCreateParams.builder()
              .model(model.getValue())
              .addTool(CreateGoal.class)
              .addTool(CreateMilestone.class)
              .addTool(CreateQuest.class);

      // add system prompt from database
      String systemPrompt = promptTemplateService.getByName("default_system_prompt").getContent();
      paramsBuilder.addSystemMessage(systemPrompt);

      for (MessageDto msg : messages) {
        String role = msg.getRole().toString().toLowerCase();
        List<ContentDto> contents = msg.getContent();

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

      // Token usage 저장
      if (chatCompletion.usage().isPresent()) {
        saveTokenUsage(
            user, message, model, chatCompletion.usage().get(), "chat_completion_with_tools");
      }

      return chatCompletion;
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI chat completion with tools", e);
    }
  }

  // 기존 호환성을 위한 오버로드 메서드
  public ChatCompletion chatCompletionWithTools(List<MessageDto> messages, Model model) {
    return chatCompletionWithTools(messages, model, null, null);
  }

  private void saveTokenUsage(
      User user, Message message, Model model, Object usage, String requestType) {
    if (user != null && usage != null) {
      try {
        // Reflection을 사용해서 usage 정보 추출 (SDK 버전 호환성을 위해)
        Class<?> usageClass = usage.getClass();
        Object promptTokensObj = usageClass.getMethod("promptTokens").invoke(usage);
        Object completionTokensObj = usageClass.getMethod("completionTokens").invoke(usage);
        Object totalTokensObj = usageClass.getMethod("totalTokens").invoke(usage);

        // Long을 Integer로 안전하게 변환
        Integer promptTokens =
            promptTokensObj instanceof Long
                ? ((Long) promptTokensObj).intValue()
                : (Integer) promptTokensObj;
        Integer completionTokens =
            completionTokensObj instanceof Long
                ? ((Long) completionTokensObj).intValue()
                : (Integer) completionTokensObj;
        Integer totalTokens =
            totalTokensObj instanceof Long
                ? ((Long) totalTokensObj).intValue()
                : (Integer) totalTokensObj;

        TokenUsage tokenUsage =
            TokenUsage.create(
                user, message, model, promptTokens, completionTokens, totalTokens, requestType);
        tokenUsageRepository.save(tokenUsage);
      } catch (Exception e) {
        // Usage 저장 실패해도 main flow는 계속 진행
        System.err.println("Failed to save token usage: " + e.getMessage());
      }
    }
  }
}
