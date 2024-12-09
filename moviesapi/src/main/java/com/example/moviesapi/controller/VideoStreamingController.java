//package com.example.moviesapi.controller;
//
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.nio.channels.Channels;
//import java.nio.channels.SeekableByteChannel;
//import java.nio.file.*;
//
//@RestController
//@RequestMapping("/api/movies")
//public class VideoStreamingController {
//
//    private final Path moviesDirectory = Paths.get("D:/Apache24/Apache24/htdocs/movies");
//
//    @GetMapping("/{filename:.+}")
//    public ResponseEntity<Resource> streamVideo(@PathVariable String filename, @RequestHeader HttpHeaders headers) throws IOException {
//        Path filePath = moviesDirectory.resolve(filename).normalize();
//        Resource resource = new InputStreamResource(Files.newInputStream(filePath));
//
//        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        long fileLength = Files.size(filePath);
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
//
//        // Определение Content-Type на основе расширения файла
//        String contentType = "video/mp4"; // По умолчанию
//        if (filename.endsWith(".mov")) {
//            contentType = "video/quicktime";
//        } else if (filename.endsWith(".avi")) {
//            contentType = "video/x-msvideo";
//        }
//        // Добавьте другие форматы по необходимости
//
//        responseHeaders.setContentType(MediaType.parseMediaType(contentType));
//
//        String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
//        if (rangeHeader == null) {
//            // Возвращаем весь файл
//            responseHeaders.setContentLength(fileLength);
//            return new ResponseEntity<>(resource, responseHeaders, HttpStatus.OK);
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
//        // Открываем поток с указанного диапазона
//        SeekableByteChannel byteChannel = Files.newByteChannel(filePath, StandardOpenOption.READ);
//        byteChannel.position(rangeStart);
//        InputStreamResource inputStreamResource = new InputStreamResource(Channels.newInputStream(byteChannel)) {
//            @Override
//            public long contentLength() throws IOException {
//                return contentLength;
//            }
//        };
//
//        responseHeaders.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
//        responseHeaders.setContentLength(contentLength);
//
//        return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.PARTIAL_CONTENT);
//    }
//}
