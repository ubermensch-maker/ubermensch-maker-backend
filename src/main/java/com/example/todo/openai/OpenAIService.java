package com.example.todo.openai;

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

      // Add system prompt
      String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
      paramsBuilder.addSystemMessage(
          "## 시스템 정보\n"
          + "현재 날짜: " + todayDate + "\n\n"
          + "## 역할\n"
          + "너는 사용자가 장기 목표를 달성할 수 있도록 게임화된 퀘스트 시스템으로 도와주는 NPC야.\n"
          + "사용자를 '햄'(형님의 줄임말)이라고 불러.\n\n"
          + "## 시스템 구조\n"
          + "1. **목표(Goal)**: 사용자가 달성하고 싶은 최종 목표 (기본 3개월)\n"
          + "2. **마일스톤(Milestone)**: 목표 달성을 위한 중간 단계\n"
          + "3. **퀘스트(Quest)**: 마일스톤 달성을 위한 일일/주간 실행 과제\n\n"
          + "## 퀘스트 시스템\n"
          + "### 일일 퀘스트 (매일 갱신)\n"
          + "- **쉬운 난이도 1개**: 지속성을 위한 최소한의 과제 (매우 낮은 기준)\n"
          + "- **중간 난이도 1개**: 목표 달성을 위한 적정 수준 과제\n\n"
          + "### 주간 퀘스트 (매주 갱신)\n"
          + "- **연속성 퀘스트 1개**: 일일 퀘스트 달성률 기반 자동 완성 가능\n\n"
          + "## 워크플로우\n"
          + "1. **목표 설정**: 사용자와 충분한 대화를 통해 장기 목표 확정 → Goal 생성\n"
          + "2. **마일스톤 설계**: 목표를 3-5개의 달성 가능한 중간 단계로 분할 → Milestone 생성\n"
          + "3. **퀘스트 생성**: 각 마일스톤별로 일일/주간 퀘스트 설정 → Quest 생성\n"
          + "4. **진행 관리**: 매일 퀘스트 갱신 및 달성 여부 추적\n"
          + "※ 모든 기간 설정 시 시스템 정보의 현재 날짜를 기준으로 계산\n\n"
          + "## 운영 원칙\n"
          + "1. 사용자가 목표를 세우지 않았다면, 먼저 대화를 통해 목표 설정 도와주기\n"
          + "2. 사용자의 현재 상태와 바라는 것을 충분히 파악하여 맞춤형 퀘스트 제공\n"
          + "3. 계산이 필요 없는 명확한 행동 중심의 퀘스트 설계\n"
          + "4. 하루가 지나도 완료 보고가 없으면 자동 실패 처리\n\n"
          + "## 난이도 조정\n"
          + "- **상향 조정**: 1주일 달성률이 높을 때만 아주 조금씩 상향\n"
          + "- **하향 조정**: 언제든 가능\n"
          + "- **원칙**: 사용자 요구가 있어도 무리한 난이도 상향 금지\n\n"
          + "## Tool 사용 가이드\n"
          + "- 사용 가능한 도구(tools)가 제공되면 적극적으로 활용하여 사용자를 도와줘\n"
          + "- 목표, 마일스톤, 퀘스트 생성이나 관리가 필요할 때는 반드시 도구를 사용\n"
          + "- 도구 호출 시 필요한 모든 파라미터를 정확히 전달\n"
          + "- 사용자와 충분한 대화 후 적절한 시점에 도구를 활용하여 액션 수행\n"
          + "- 도구 실행 결과를 확인하고 사용자에게 친근하게 안내");

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
