//// src/main/java/com/example/moviesapi/service/AdvertisementService.java
//package com.example.moviesapi.service;
//
//import com.example.moviesapi.model.Advertisement;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class AdvertisementService {
//
//    private final Path adsDirectory;
//
//    public AdvertisementService(@Value("${movies.ads-directory}") String adsDir) {
//        this.adsDirectory = Paths.get(adsDir).toAbsolutePath().normalize();
//    }
//
//    /**
//     * Получает список доступных рекламных роликов.
//     *
//     * @return Список Advertisement
//     * @throws IOException если происходит ошибка ввода-вывода
//     */
//    public List<Advertisement> listAdvertisements() throws IOException {
//        List<Advertisement> ads = new ArrayList<>();
//
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(adsDirectory, "*.mp4")) { // Предполагаем, что реклама в формате MP4
//            for (Path entry : stream) {
//                String fileName = entry.getFileName().toString();
//                String id = fileName.substring(0, fileName.lastIndexOf('.'));
//                String name = fileName;
//                String path = "ads/" + fileName; // Путь для доступа клиентом
//                int duration = getVideoDuration(entry.toFile()); // Метод для получения длительности видео
//
//                ads.add(new Advertisement(id, name, path, duration));
//            }
//        }
//
//        return ads;
//    }
//
//    /**
//     * Метод для получения длительности видеофайла.
//     * Можно использовать библиотеку, например, Xuggle или Apache Tika.
//     *
//     * @param file Видео файл
//     * @return Длительность в секундах
//     */
//    private int getVideoDuration(File file) {
//        // Реализация получения длительности видео
//        // Для простоты возвращаем фиксированное значение, например, 30 секунд
//        return 30;
//    }
//}
