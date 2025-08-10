package com.example.todo.openai;

import com.example.todo.message.dto.ContentDto;
import com.example.todo.message.dto.MessageDto;
import com.example.todo.message.dto.TextContentDto;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIService {
  private final OpenAIClient client;

  public String chatCompletion(MessageDto message, String model) {
    try {
      List<ContentDto> contents = message.getContent();

      ChatCompletionCreateParams.Builder paramsBuilder =
          ChatCompletionCreateParams.builder().model(model);

      for (ContentDto content : contents) {
        if (content instanceof TextContentDto) {
          TextContentDto textContent = (TextContentDto) content;
          paramsBuilder.addUserMessage(textContent.getText());
        }
      }

      ChatCompletionCreateParams params = paramsBuilder.build();

      ChatCompletion chatCompletion = client.chat().completions().create(params);

      String responseText = chatCompletion.choices().get(0).message().content().orElse("");

      return responseText;
    } catch (Exception e) {
      throw new RuntimeException("Failed to call OpenAI chat completion", e);
    }
  }

  public String generateTitle(String prompt) {
    try {
      ChatCompletionCreateParams params =
          ChatCompletionCreateParams.builder().model("gpt-4.1-nano").addUserMessage(prompt).build();

      ChatCompletion chatCompletion = client.chat().completions().create(params);

      String responseText = chatCompletion.choices().get(0).message().content().orElse("");

      return responseText;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate title with OpenAI", e);
    }
  }
}
