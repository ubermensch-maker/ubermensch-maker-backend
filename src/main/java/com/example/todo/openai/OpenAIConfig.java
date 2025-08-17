package com.example.todo.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

  @Bean
  public OpenAIClient openAiClient() {
    return OpenAIOkHttpClient.fromEnv();
  }
}
