//package com.example.moviesapi.controller;
//
//import com.example.moviesapi.model.Movie;
//import com.example.moviesapi.service.MoviesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@CrossOrigin(origins = "*") // Разрешить CORS для всех источников
//@RestController
//@RequestMapping("/api/movies")
//public class MoviesController {
//
//    @Autowired
//    private MoviesService moviesService;
//
//    @GetMapping
//    public List<Movie> getMovies() throws IOException {
//        return moviesService.getMovies("");
//    }
//}
