package com.cbfacademy.horoscopeapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        String name = payload.get("name");
        LocalDate dob = LocalDate.parse(payload.get("dateOfBirth"));

        LocalTime timeOfBirth = payload.containsKey("timeOfBirth") ? LocalTime.parse(payload.get("timeOfBirth")) : null;
        String placeOfBirth = payload.getOrDefault("placeOfBirth", null);

        UserProfile user = userService.createUser(name, dob, timeOfBirth, placeOfBirth);
        return ResponseEntity.created(URI.create("/api/users/" + user.getId())).body(user);
    }

    // Get one user by ID
    @GetMapping("/{id}")
    public UserProfile getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    // Get all users
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Daily, weekly, monthly readings
    @GetMapping("/{id}/horoscope/daily")
    public ResponseEntity<String> getDailyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getDailyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @GetMapping("/{id}/horoscope/weekly")
    public ResponseEntity<String> getWeeklyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getWeeklyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @GetMapping("/{id}/horoscope/monthly")
    public ResponseEntity<String> getMonthlyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getMonthlyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @PostMapping("/{id}/calculate-signs")
    public ResponseEntity<UserProfile> calculateSigns(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        userService.updateSigns(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates) {
        UserProfile updatedUser = userService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }
}