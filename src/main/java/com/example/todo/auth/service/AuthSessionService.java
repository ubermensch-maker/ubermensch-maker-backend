package com.example.todo.auth.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class AuthSessionService {

  private final Map<String, String> sessionTokenMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public String createSession(String token) {
    String sessionId = UUID.randomUUID().toString();
    sessionTokenMap.put(sessionId, token);

    // 5분 후 자동 삭제
    scheduler.schedule(() -> sessionTokenMap.remove(sessionId), 5, TimeUnit.MINUTES);

    return sessionId;
  }

  public String getTokenAndRemove(String sessionId) {
    return sessionTokenMap.remove(sessionId);
  }
}
