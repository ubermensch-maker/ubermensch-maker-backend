package com.example.todo.usage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenUsageRepository extends JpaRepository<TokenUsage, UUID> {

  List<TokenUsage> findByUserIdOrderByCreatedAtDesc(Long userId);

  @Query("SELECT SUM(tu.totalTokens) FROM TokenUsage tu WHERE tu.user.id = :userId")
  Long getTotalTokensByUser(@Param("userId") Long userId);

  @Query(
      "SELECT SUM(tu.totalTokens) FROM TokenUsage tu WHERE tu.user.id = :userId AND tu.createdAt >="
          + " :startDate")
  Long getTotalTokensByUserSince(
      @Param("userId") Long userId, @Param("startDate") Instant startDate);
}
