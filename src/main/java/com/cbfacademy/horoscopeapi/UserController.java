package com.cbfacademy.horoscopeapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final HoroscopeService horoscopeService;

    public UserController(UserService userService, HoroscopeService horoscopeService) {
        this.userService = userService;
        this.horoscopeService = horoscopeService;
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<UserProfile> createUser(@RequestBody Map<String, String> payload) {
        return null; 
    }

    // Get one user by ID
    @GetMapping("/{id}")
    public UserProfile getUser(@PathVariable Long id) {
        return null;
    }

    // Get all users
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return null;
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return null;
    }
}