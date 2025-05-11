package com.example.todo.api;

import com.example.todo.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserApiTest {
    RestClient restClient = RestClient.create("http://localhost:8080");

    static final long TEST_USER_ID = 1L;

    @Test
    void createTest() {
        UserDto response = create(new UserCreateDto(
                "test@gmail.com", "password", "name", "Asia/Seoul"
        ));
        System.out.println("response = " + response);
    }

    UserDto create(UserCreateDto request) {
        return restClient.post()
                .uri("/users")
                .body(request)
                .retrieve()
                .body(UserDto.class);
    }

    @Test
    void readTest() {
        UserDto response = read(TEST_USER_ID);
        System.out.println("response = " + response);
    }

    UserDto read(Long userId) {
        return restClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .body(UserDto.class);
    }

    @Test
    void updateTest() {
        update(TEST_USER_ID, new UserUpdateDto(null, "updated password", "new name", null));
        UserDto response = read(TEST_USER_ID);
        System.out.println("response = " + response);
    }

    void update(Long userId, UserUpdateDto request) {
        restClient.put()
                .uri("/users/{userId}", userId)
                .body(request)
                .retrieve()
                .body(UserDto.class);
    }

    @Test
    void deleteTest() {
        delete(TEST_USER_ID);
        assertThrows(Exception.class, () -> read(TEST_USER_ID));
    }

    void delete(Long userId) {
        restClient.delete()
                .uri("/users/{userId}", userId)
                .retrieve()
                .body(Void.class);
    }

    @Getter
    @AllArgsConstructor
    static class UserCreateDto {
        private String email;
        private String password;
        private String name;
        private String timezone;
    }

    @Getter
    @AllArgsConstructor
    static class UserUpdateDto {
        private String email;
        private String password;
        private String name;
        private String timezone;
    }
}
