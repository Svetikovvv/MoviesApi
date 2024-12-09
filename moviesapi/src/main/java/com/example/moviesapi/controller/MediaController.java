package com.example.moviesapi.controller;//package com.example.moviesapi.controller;
//
//import com.example.moviesapi.model.MediaItem;
//import com.example.moviesapi.service.MoviesService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.nio.channels.Channels;
//import java.nio.channels.SeekableByteChannel;
//import java.nio.file.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/media")
//public class MediaController {
//
//    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);  // Логгер
//    private final MoviesService moviesService;
//    private final Path mediaDirectory;
//
//    public MediaController(MoviesService moviesService, @Value("${movies.directory}") String moviesDir) {
//        this.moviesService = moviesService;
//        this.mediaDirectory = Paths.get(moviesDir).toAbsolutePath().normalize();
//    }
//
//    @GetMapping
//    public ResponseEntity<List<MediaItem>> listMedia(@RequestParam(required = false) String path) throws IOException {
//        logger.debug("Fetching media list for path: {}", path != null ? path : "root");
//
//        List<MediaItem> items = moviesService.getMovies(path != null ? path : "");
//
//        if (items == null || items.isEmpty()) {
//            logger.warn("No media found for path: {}", path);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        logger.info("Successfully fetched {} media items", items.size());
//        return ResponseEntity.ok(items);
//    }
//
//    @GetMapping("/stream/{path:.+}")
//    public ResponseEntity<Resource> streamVideo(@PathVariable String path, @RequestHeader HttpHeaders headers) throws IOException {
//        Path filePath = mediaDirectory.resolve(path).normalize();
//        logger.debug("Streaming video for file: {}", filePath);
//
//        // Дополнительная проверка безопасности
//        if (!filePath.startsWith(mediaDirectory)) {
//            logger.error("Attempted to access file outside of media directory: {}", filePath);
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//        }
//
//        if (!Files.exists(filePath) || !Files.isReadable(filePath) || Files.isDirectory(filePath)) {
//            logger.warn("File not found or unreadable: {}", filePath);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        long fileLength = Files.size(filePath);
//        String contentType = determineContentType(filePath.getFileName().toString());
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
//        responseHeaders.setContentType(MediaType.parseMediaType(contentType));
//
//        String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
//        if (rangeHeader == null) {
//            responseHeaders.setContentLength(fileLength);
//            return ResponseEntity.ok()
//                    .headers(responseHeaders)
//                    .body(new InputStreamResource(Files.newInputStream(filePath)));
//        }
//
//        // Обработка диапазонных запросов
//        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
//        long rangeStart = Long.parseLong(ranges[0]);
//        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;
//
//        if (rangeEnd >= fileLength) {
//            rangeEnd = fileLength - 1;
//        }
//
//        long contentLength = rangeEnd - rangeStart + 1;
//
//        SeekableByteChannel byteChannel = Files.newByteChannel(filePath, StandardOpenOption.READ);
//        byteChannel.position(rangeStart);
//        InputStreamResource inputStreamResource = new InputStreamResource(Channels.newInputStream(byteChannel)) {
//            @Override
//            public long contentLength() {
//                return contentLength;
//            }
//        };
//
//        responseHeaders.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
//        responseHeaders.setContentLength(contentLength);
//
//        logger.info("Serving partial content: {}-{} of {}", rangeStart, rangeEnd, fileLength);
//        return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.PARTIAL_CONTENT);
//    }
//
//    private String determineContentType(String filename) {
//        if (filename.endsWith(".mov")) {
//            return "video/quicktime";
//        } else if (filename.endsWith(".avi")) {
//            return "video/x-msvideo";
//        } else if (filename.endsWith(".mp4")) {
//            return "video/mp4";
//        } else if (filename.endsWith(".flv")) {
//            return "video/x-flv";
//        }
//        return "application/octet-stream";
//    }
//}

import com.example.moviesapi.model.MediaItem;
import com.example.moviesapi.service.MoviesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*") // Настройте CORS по необходимости
public class MediaController {

    private final MoviesService moviesService;
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class); // Инициализация логгера

    public MediaController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    /**
     * Эндпоинт для получения списка медиафайлов в указанном пути.
     * Если путь null или пустой, возвращает корневую директорию.
     *
     * @param path Относительный путь от базовой директории
     * @return Список MediaItem
     */
    @GetMapping
    public ResponseEntity<?> listMedia(@RequestParam(required = false) String path) {
        try {
            List<MediaItem> mediaItems = moviesService.listMedia(path);
            return ResponseEntity.ok(mediaItems);
        } catch (IOException e) {
            logger.error("Ошибка при получении медиафайлов по пути: {}", path, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Путь не существует или не является директорией: " + path);
        }
    }

    /**
     * Эндпоинт для стриминга видеофайла.
     *
     * @param rangeHeader Заголовок Range для частичного контента
     *      @return Стриминг видео контента
     */
    @GetMapping("/stream/**")
    public ResponseEntity<?> streamVideo(@RequestHeader(value = "Range", required = false) String rangeHeader, HttpServletRequest request) {
        // Извлекаем путь с вложенными папками
        String path = extractPathFromPattern(request);

        try {
            // Декодируем путь, чтобы восстанавливать символы '/'
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

            // Теперь разрешаем путь
            Path videoPath = moviesService.resolvePath(decodedPath);

            if (!Files.exists(videoPath) || !Files.isRegularFile(videoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Файл не найден");
            }

            long fileSize = Files.size(videoPath);
            InputStream inputStream = Files.newInputStream(videoPath, StandardOpenOption.READ);

            // Обработка Range-запроса
            if (StringUtils.hasText(rangeHeader)) {
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                long start = Long.parseLong(ranges[0]);
                long end = ranges.length > 1 && StringUtils.hasText(ranges[1]) ? Long.parseLong(ranges[1]) : fileSize - 1;

                if (start > end || end >= fileSize) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
                }

                long contentLength = end - start + 1;
                inputStream.skip(start);

                HttpHeaders headers = new HttpHeaders();
                String mimeType = Files.probeContentType(videoPath);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                headers.setContentType(MediaType.parseMediaType(mimeType));
                headers.setContentLength(contentLength);
                headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
                headers.add("Accept-Ranges", "bytes");

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(new InputStreamResource(new LimitedInputStream(inputStream, contentLength)));
            } else {
                HttpHeaders headers = new HttpHeaders();
                String mimeType = Files.probeContentType(videoPath);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                headers.setContentType(MediaType.parseMediaType(mimeType));
                headers.setContentLength(fileSize);
                headers.add("Accept-Ranges", "bytes");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));
            }
        } catch (IOException e) {
            logger.error("Ошибка при стриминге видео по пути: {}", path, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при стриминге видео");
        }
    }



    /**
     * Извлекает путь из URL запроса после /stream/
     *
     * @param request HttpServletRequest
     * @return Относительный путь как String
     */
    private String extractPathFromPattern(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String streamPath = "/api/media/stream/";
        int index = requestURI.indexOf(streamPath);
        if (index != -1) {
            // Извлекаем путь, начиная после /stream/
            return requestURI.substring(index + streamPath.length());
        }
        return "";
    }


    /**
     * Ограниченный InputStream для частичного контента.
     */
    private static class LimitedInputStream extends InputStream {
        private final InputStream in;
        private long remaining;

        public LimitedInputStream(InputStream in, long limit) {
            this.in = in;
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) return -1;
            int result = in.read();
            if (result != -1) remaining--;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) return -1;
            len = (int) Math.min(len, remaining);
            int result = in.read(b, off, len);
            if (result != -1) remaining -= result;
            return result;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}



//import com.example.moviesapi.model.Advertisement;
//import com.example.moviesapi.model.MediaItem;
//import com.example.moviesapi.model.UserProgress;
//import com.example.moviesapi.service.AdvertisementService;
//import com.example.moviesapi.service.MoviesService;
//import com.example.moviesapi.service.UserProgressService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.*;
//        import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//        import jakarta.servlet.http.HttpServletRequest;
//import java.io.*;
//        import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//        import java.util.List;
//
//@RestController
//@RequestMapping("/api/media")
//@CrossOrigin(origins = "*") // Настройте CORS по необходимости
//public class MediaController {
//
//    private final MoviesService moviesService;
//    private final AdvertisementService advertisementService;
//    private final UserProgressService userProgressService;
//    private static final Logger logger = LoggerFactory.getLogger(MediaController.class); // Инициализация логгера
//
//    public MediaController(MoviesService moviesService, AdvertisementService advertisementService, UserProgressService userProgressService) {
//        this.moviesService = moviesService;
//        this.advertisementService = advertisementService;
//        this.userProgressService = userProgressService;
//    }
//
//    /**
//     * Эндпоинт для получения списка медиафайлов в указанном пути.
//     * Если путь null или пустой, возвращает корневую директорию.
//     *
//     * @param path Относительный путь от базовой директории
//     * @return Список MediaItem
//     */
//    @GetMapping
//    public ResponseEntity<?> listMedia(@RequestParam(required = false) String path) {
//        try {
//            List<MediaItem> mediaItems = moviesService.listMedia(path);
//            return ResponseEntity.ok(mediaItems);
//        } catch (IOException e) {
//            logger.error("Ошибка при получении медиафайлов по пути: {}", path, e);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Путь не существует или не является директорией: " + path);
//        }
//    }
//
//    /**
//     * Эндпоинт для стриминга видеофайла.
//     *
//     * @param rangeHeader Заголовок Range для частичного контента
//     * @return Стриминг видео контента
//     */
//    @GetMapping("/stream/**")
//    public ResponseEntity<?> streamVideo(@RequestHeader(value = "Range", required = false) String rangeHeader, HttpServletRequest request) {
//        // Извлекаем путь с вложенными папками
//        String path = extractPathFromPattern(request);
//
//        try {
//            // Декодируем путь, чтобы восстанавливать символы '/'
//            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
//
//            // Теперь разрешаем путь
//            Path videoPath = moviesService.resolvePath(decodedPath);
//
//            if (!Files.exists(videoPath) || !Files.isRegularFile(videoPath)) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Файл не найден");
//            }
//
//            long fileSize = Files.size(videoPath);
//            InputStream inputStream = Files.newInputStream(videoPath, StandardOpenOption.READ);
//
//            // Обработка Range-запроса
//            if (StringUtils.hasText(rangeHeader)) {
//                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
//                long start = Long.parseLong(ranges[0]);
//                long end = ranges.length > 1 && StringUtils.hasText(ranges[1]) ? Long.parseLong(ranges[1]) : fileSize - 1;
//
//                if (start > end || end >= fileSize) {
//                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
//                }
//
//                long contentLength = end - start + 1;
//                inputStream.skip(start);
//
//                HttpHeaders headers = new HttpHeaders();
//                String mimeType = Files.probeContentType(videoPath);
//                if (mimeType == null) {
//                    mimeType = "application/octet-stream";
//                }
//                headers.setContentType(MediaType.parseMediaType(mimeType));
//                headers.setContentLength(contentLength);
//                headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
//                headers.add("Accept-Ranges", "bytes");
//
//                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                        .headers(headers)
//                        .body(new InputStreamResource(new LimitedInputStream(inputStream, contentLength)));
//            } else {
//                HttpHeaders headers = new HttpHeaders();
//                String mimeType = Files.probeContentType(videoPath);
//                if (mimeType == null) {
//                    mimeType = "application/octet-stream";
//                }
//                headers.setContentType(MediaType.parseMediaType(mimeType));
//                headers.setContentLength(fileSize);
//                headers.add("Accept-Ranges", "bytes");
//
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .body(new InputStreamResource(inputStream));
//            }
//        } catch (IOException e) {
//            logger.error("Ошибка при стриминге видео по пути: {}", path, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при стриминге видео");
//        }
//    }
//
//    /**
//     * Извлекает путь из URL запроса после /stream/
//     *
//     * @param request HttpServletRequest
//     * @return Относительный путь как String
//     */
//    private String extractPathFromPattern(HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        String contextPath = request.getContextPath();
//        String streamPath = "/api/media/stream/";
//        int index = requestURI.indexOf(streamPath);
//        if (index != -1) {
//            // Извлекаем путь, начиная после /stream/
//            return requestURI.substring(index + streamPath.length());
//        }
//        return "";
//    }
//
//    /**
//     * Ограниченный InputStream для частичного контента.
//     */
//    private static class LimitedInputStream extends InputStream {
//        private final InputStream in;
//        private long remaining;
//
//        public LimitedInputStream(InputStream in, long limit) {
//            this.in = in;
//            this.remaining = limit;
//        }
//
//        @Override
//        public int read() throws IOException {
//            if (remaining <= 0) return -1;
//            int result = in.read();
//            if (result != -1) remaining--;
//            return result;
//        }
//
//        @Override
//        public int read(byte[] b, int off, int len) throws IOException {
//            if (remaining <= 0) return -1;
//            len = (int) Math.min(len, remaining);
//            int result = in.read(b, off, len);
//            if (result != -1) remaining -= result;
//            return result;
//        }
//
//        @Override
//        public void close() throws IOException {
//            in.close();
//        }
//    }
//    /**
//     * Эндпоинт для получения списка рекламных роликов.
//     *
//     * @return Список Advertisement
//     */
//    @GetMapping("/ads")
//    public ResponseEntity<?> listAdvertisements() {
//        try {
//            List<Advertisement> ads = advertisementService.listAdvertisements();
//            return ResponseEntity.ok(ads);
//        } catch (IOException e) {
//            logger.error("Ошибка при получении рекламных роликов", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении рекламных роликов");
//        }
//    }
//    /**
//     * Эндпоинт для обновления прогресса пользователя после просмотра рекламы.
//     *
//     * @param userId    Уникальный идентификатор пользователя
//     * @param moviePath Путь к фильму
//     * @return Статус обновления
//     */
//    @PostMapping("/watchAd")
//    public ResponseEntity<?> watchAd(@RequestParam String userId, @RequestParam String moviePath) {
//        UserProgress progress = userProgressService.getUserProgress(userId);
//        progress.incrementAdCount(moviePath);
//        logger.info("Пользователь {} просмотрел рекламу для фильма {}", userId, moviePath);
//        return ResponseEntity.ok("Реклама просмотрена");
//    }
//
//    /**
//     * Эндпоинт для проверки, может ли пользователь смотреть фильм.
//     *
//     * @param userId    Уникальный идентификатор пользователя
//     * @param moviePath Путь к фильму
//     * @return Статус доступа
//     */
//    @GetMapping("/canWatch")
//    public ResponseEntity<?> canWatch(@RequestParam String userId, @RequestParam String moviePath) {
//        try {
//            List<MediaItem> mediaItems = moviesService.listMedia(null); // Предполагается, что path = null для получения всех фильмов
//            MediaItem movie = mediaItems.stream()
//                    .filter(item -> !item.isFolder() && item.getPath().equals(moviePath))
//                    .findFirst()
//                    .orElse(null);
//
//            if (movie == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Фильм не найден");
//            }
//
//            UserProgress progress = userProgressService.getUserProgress(userId);
//            int watchedAds = progress.getAdCount(moviePath);
//            int requiredAds = movie.getRequiredAds();
//
//            if (watchedAds >= requiredAds) {
//                return ResponseEntity.ok("Доступ разрешён");
//            } else {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body("Требуется посмотреть ещё " + (requiredAds - watchedAds) + " реклам");
//            }
//        } catch (IOException e) {
//            logger.error("Ошибка при проверке доступа к фильму: {}", moviePath, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера");
//        }
//    }
//}