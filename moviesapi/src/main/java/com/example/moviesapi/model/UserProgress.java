//// src/main/java/com/example/moviesapi/model/UserProgress.java
//package com.example.moviesapi.model;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class UserProgress {
//    private String userId; // Уникальный идентификатор пользователя (например, UUID)
//    private Map<String, Integer> movieAdCount; // Карта: путь к фильму -> количество просмотренных реклам
//
//    public UserProgress(String userId) {
//        this.userId = userId;
//        this.movieAdCount = new HashMap<>();
//    }
//
//    // Геттеры и Сеттеры
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public Map<String, Integer> getMovieAdCount() {
//        return movieAdCount;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public void setMovieAdCount(Map<String, Integer> movieAdCount) {
//        this.movieAdCount = movieAdCount;
//    }
//
//    public void incrementAdCount(String moviePath) {
//        this.movieAdCount.put(moviePath, this.movieAdCount.getOrDefault(moviePath, 0) + 1);
//    }
//
//    public int getAdCount(String moviePath) {
//        return this.movieAdCount.getOrDefault(moviePath, 0);
//    }
//}
