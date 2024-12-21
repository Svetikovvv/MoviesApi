package com.example.moviesapi.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final Map<String, Long> connectedUsers = new HashMap<>();

    @PostMapping("/connect")
    public ResponseEntity<String> connectUser(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный идентификатор пользователя.");
        }

        // Сохранение пользователя в памяти
        connectedUsers.put(userId, System.currentTimeMillis());

        return ResponseEntity.ok("Пользователь успешно подключен. ID: " + userId);
    }
}
