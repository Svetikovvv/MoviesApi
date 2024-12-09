//// src/main/java/com/example/moviesapi/service/UserProgressService.java
//package com.example.moviesapi.service;
//
//import com.example.moviesapi.model.UserProgress;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class UserProgressService {
//
//    // Используем ConcurrentHashMap для потокобезопасности
//    private final ConcurrentHashMap<String, UserProgress> userProgressMap = new ConcurrentHashMap<>();
//
//    /**
//     * Получает или создаёт прогресс для пользователя.
//     *
//     * @param userId Уникальный идентификатор пользователя
//     * @return Объект UserProgress
//     */
//    public UserProgress getUserProgress(String userId) {
//        return userProgressMap.computeIfAbsent(userId, UserProgress::new);
//    }
//
//    /**
//     * Сброс прогресса пользователя для конкретного фильма.
//     *
//     * @param userId    Уникальный идентификатор пользователя
//     * @param moviePath Путь к фильму
//     */
//    public void resetProgress(String userId, String moviePath) {
//        UserProgress progress = userProgressMap.get(userId);
//        if (progress != null) {
//            progress.getMovieAdCount().remove(moviePath);
//        }
//    }
//
//    /**
//     * Сброс всего прогресса пользователя.
//     *
//     * @param userId Уникальный идентификатор пользователя
//     */
//    public void resetAllProgress(String userId) {
//        userProgressMap.remove(userId);
//    }
//}
