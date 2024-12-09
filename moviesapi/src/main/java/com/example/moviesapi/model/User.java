//package com.example.moviesapi.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//
//import java.util.Set;
//@Data
//@Entity
//@Table(name = "users")
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    // Роли пользователя (например, USER, ADMIN)
//    @ElementCollection(fetch = FetchType.EAGER)
//    private Set<String> roles;
//
//    // Constructors, Getters и Setters
//    public User() {}
//
//    public User(String username, String password, Set<String> roles) {
//        this.username = username;
//        this.password = password;
//        this.roles = roles;
//    }
//
//    // Getters and Setters
//    // ...
//}
