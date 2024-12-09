package com.example.moviesapi.model;// src/main/java/com/example/kzcinema/models/MediaItem.java

import com.fasterxml.jackson.annotation.JsonProperty;


public class MediaItem {
    private String name;
    private String path;

    @JsonProperty("folder")
    private boolean isFolder;

    public MediaItem() {}

    public MediaItem(String name, boolean isFolder, String path) {
        this.name = name;
        this.isFolder = isFolder;
        this.path = path;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

//// src/main/java/com/example/moviesapi/model/MediaItem.java
//package com.example.moviesapi.model;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import java.util.List;
//
//public class MediaItem {
//    private String name;
//    private String path;
//
//    @JsonProperty("folder")
//    private boolean isFolder;
//
//    private int requiredAds; // Количество требуемых реклам
//
//    private List<Advertisement> advertisements; // Список рекламных роликов
//
//    public MediaItem() {}
//
//    public MediaItem(String name, boolean isFolder, String path, int requiredAds, List<Advertisement> advertisements) {
//        this.name = name;
//        this.isFolder = isFolder;
//        this.path = path;
//        this.requiredAds = requiredAds;
//        this.advertisements = advertisements;
//    }
//
//    // Геттеры и Сеттеры
//
//    public String getName() {
//        return name;
//    }
//
//    public boolean isFolder() {
//        return isFolder;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public int getRequiredAds() {
//        return requiredAds;
//    }
//
//    public List<Advertisement> getAdvertisements() {
//        return advertisements;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setFolder(boolean folder) {
//        isFolder = folder;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public void setRequiredAds(int requiredAds) {
//        this.requiredAds = requiredAds;
//    }
//
//    public void setAdvertisements(List<Advertisement> advertisements) {
//        this.advertisements = advertisements;
//    }
//}
