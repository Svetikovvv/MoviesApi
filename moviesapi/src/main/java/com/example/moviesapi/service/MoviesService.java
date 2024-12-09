//package com.example.moviesapi.service;
//
//import com.example.moviesapi.model.MediaItem;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class MoviesService {
//
//    private static final Logger logger = LoggerFactory.getLogger(MoviesService.class);  // Логгер
//    private final Path mediaDirectory;
//
//    public MoviesService(@Value("${movies.directory}") String moviesDir) {
//        this.mediaDirectory = Paths.get(moviesDir).toAbsolutePath().normalize();
//    }
//
//    public List<MediaItem> getMovies(String path) throws IOException {
//        List<MediaItem> mediaItems = new ArrayList<>();
//        Path dir = mediaDirectory.resolve(path).normalize();
//
//        logger.debug("Fetching movies from directory: {}", dir);
//
//        // Проверка безопасности
//        if (!dir.startsWith(mediaDirectory) || !Files.isDirectory(dir)) {
//            logger.warn("Attempted to access directory outside of media directory or invalid directory: {}", dir);
//            return mediaItems; // Возвращаем пустой список, если путь некорректен
//        }
//
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
//            for (Path entry : stream) {
//                String relativePath = mediaDirectory.relativize(entry).toString().replace("\\", "/");
//                // Преобразуем путь к файлу для клиента
//                String mediaPath = path + relativePath;
//                boolean isDirectory = Files.isDirectory(entry) && !entry.getFileName().toString().startsWith(".");
//                MediaItem mediaItem = new MediaItem(entry.getFileName().toString(), isDirectory, mediaPath);
//                mediaItems.add(mediaItem);
//                logger.debug("Found media item: {}", mediaItem.getName());
//            }
//        }
//
//        logger.info("Fetched {} media items", mediaItems.size());
//        return mediaItems;
//    }
//
//}
// src/main/java/com/example/moviesapi/service/MoviesService.java
package com.example.moviesapi.service;
// src/main/java/com/example/moviesapi/service/MoviesService.java
import com.example.moviesapi.config.MovieProperties;
import com.example.moviesapi.model.MediaItem;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoviesService {

    private static final Logger logger = LoggerFactory.getLogger(MoviesService.class);

    private final MovieProperties movieProperties;
    private Path basePath;

    public MoviesService(MovieProperties movieProperties) {
        this.movieProperties = movieProperties;
    }

    @PostConstruct
    public void init() {
        basePath = Paths.get(movieProperties.getBaseDirectory()).toAbsolutePath().normalize();
        logger.info("Movies Directory инициализирован: {}", basePath.toString());
    }

    /**
     * Получает список медиафайлов в указанном пути.
     *
     * @param path Относительный путь от базовой директории. Если null или пусто, возвращает базовую директорию.
     * @return Список объектов MediaItem, представляющих файлы и папки.
     * @throws IOException если происходит ошибка ввода-вывода.
     */
    public List<MediaItem> listMedia(String path) throws IOException {
        Path mediaPath = resolvePath(path);
        if (!Files.exists(mediaPath) || !Files.isDirectory(mediaPath)) {
            throw new IOException("Путь не существует или не является директорией: " + mediaPath.toString());
        }

        List<MediaItem> mediaItems = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(mediaPath)) {
            for (Path entry : stream) {
                String name = entry.getFileName().toString();
                boolean isFolder = Files.isDirectory(entry);
                String relativePath = basePath.relativize(entry.toAbsolutePath()).toString().replace("\\", "/");
                mediaItems.add(new MediaItem(name, isFolder, relativePath));
            }
        } catch (IOException e) {
            logger.error("Ошибка при получении списка медиа в пути: {}", mediaPath, e);
            throw e;
        }

        logger.info("Получен список из {} медиафайлов в пути: {}", mediaItems.size(), mediaPath.toString());
        return mediaItems;
    }

    /**
     * Разрешает относительный путь относительно базовой директории, предотвращая обход директорий.
     *
     * @param relativePath Относительный путь от клиента
     * @return Разрешенный Path
     * @throws IOException если путь недействителен или доступ запрещен
     */
    public Path resolvePath(String relativePath) throws IOException {
        Path targetPath = (relativePath != null && !relativePath.isEmpty()) ? basePath.resolve(relativePath).normalize() : basePath;

        if (!targetPath.startsWith(basePath)) {
            throw new IOException("Неверный путь: Попытка обхода директорий");
        }

        logger.debug("Разрешенный путь: {}", targetPath.toString());
        return targetPath;
    }


    // Дополнительные методы могут быть добавлены здесь, например, для стриминга
}


//
//package com.example.moviesapi.service;
//
//import com.example.moviesapi.config.MovieProperties;
//import com.example.moviesapi.model.MediaItem;
//import com.example.moviesapi.model.Advertisement;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.*;
//        import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class MoviesService {
//
//    private static final Logger logger = LoggerFactory.getLogger(MoviesService.class);
//
//    private final MovieProperties movieProperties;
//    private final AdvertisementService advertisementService;
//    private Path basePath;
//
//    public MoviesService(MovieProperties movieProperties, AdvertisementService advertisementService) {
//        this.movieProperties = movieProperties;
//        this.advertisementService = advertisementService;
//    }
//
//    @PostConstruct
//    public void init() {
//        basePath = Paths.get(movieProperties.getBaseDirectory()).toAbsolutePath().normalize();
//        logger.info("Movies Directory инициализирован: {}", basePath.toString());
//    }
//
//    /**
//     * Получает список медиафайлов в указанном пути с информацией о рекламе.
//     *
//     * @param path Относительный путь от базовой директории. Если null или пусто, возвращает базовую директорию.
//     * @return Список объектов MediaItem, представляющих файлы и папки.
//     * @throws IOException если происходит ошибка ввода-вывода.
//     */
//    public List<MediaItem> listMedia(String path) throws IOException {
//        Path mediaPath = resolvePath(path);
//        if (!Files.exists(mediaPath) || !Files.isDirectory(mediaPath)) {
//            throw new IOException("Путь не существует или не является директорией: " + mediaPath.toString());
//        }
//
//        List<MediaItem> mediaItems = new ArrayList<>();
//        List<Advertisement> ads = advertisementService.listAdvertisements(); // Получаем список реклам
//
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(mediaPath)) {
//            for (Path entry : stream) {
//                String name = entry.getFileName().toString();
//                boolean isFolder = Files.isDirectory(entry);
//                String relativePath = basePath.relativize(entry.toAbsolutePath()).toString().replace("\\", "/");
//
//                if (!isFolder && isVideoFile(name)) {
//                    // Определяем количество реклам в зависимости от длины фильма
//                    int requiredAds = calculateRequiredAds(entry.toFile());
//
//                    // Связываем рекламу с фильмом (например, случайным образом выбираем requiredAds реклам)
//                    List<Advertisement> selectedAds = selectAdvertisements(ads, requiredAds);
//
//                    mediaItems.add(new MediaItem(name, isFolder, relativePath, requiredAds, selectedAds));
//                } else {
//                    mediaItems.add(new MediaItem(name, isFolder, relativePath, 0, new ArrayList<>()));
//                }
//
//                logger.debug("Найден медиа элемент: {}", name);
//            }
//        }
//
//        logger.info("Получен список из {} медиафайлов в пути: {}", mediaItems.size(), mediaPath.toString());
//        return mediaItems;
//    }
//
//    private boolean isVideoFile(String filename) {
//        String lower = filename.toLowerCase();
//        return lower.endsWith(".mov") || lower.endsWith(".mp4") || lower.endsWith(".avi") || lower.endsWith(".flv");
//    }
//
//    private int calculateRequiredAds(File movieFile) {
//        // Пример: 1 реклама за каждые 30 минут фильма
//        // Для упрощения используем фиксированное количество реклам
//        return 2;
//    }
//
//    private List<Advertisement> selectAdvertisements(List<Advertisement> ads, int requiredAds) {
//        // Простая реализация: выбираем первые requiredAds реклам
//        // Можно реализовать более сложную логику, например, случайный выбор
//        return ads.subList(0, Math.min(requiredAds, ads.size()));
//    }
//
//    /**
//     * Разрешает относительный путь относительно базовой директории, предотвращая обход директорий.
//     *
//     * @param relativePath Относительный путь от клиента
//     * @return Разрешенный Path
//     * @throws IOException если путь недействителен или доступ запрещен
//     */
//    public Path resolvePath(String relativePath) throws IOException {
//        Path targetPath = (relativePath != null && !relativePath.isEmpty()) ? basePath.resolve(relativePath).normalize() : basePath;
//
//        if (!targetPath.startsWith(basePath)) {
//            throw new IOException("Неверный путь: Попытка обхода директорий");
//        }
//
//        logger.debug("Разрешенный путь: {}", targetPath.toString());
//        return targetPath;
//    }
//
//    // Дополнительные методы могут быть добавлены здесь, например, для стриминга
//}
