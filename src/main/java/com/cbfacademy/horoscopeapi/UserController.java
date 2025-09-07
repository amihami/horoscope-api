package com.cbfacademy.horoscopeapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Calculate signs from a STRICT payload:
     * {
     * "subject": { year, month, day, hour, minute, city, name, latitude, longitude,
     * timezone }
     * }
     * All fields are REQUIRED.
     */
    @PostMapping("/{id}/calculate-signs")
    public ResponseEntity<UserProfile> calculateSigns(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> payload) {

        // 1) Validate top-level "subject" object
        if (payload == null || !payload.containsKey("subject") || !(payload.get("subject") instanceof Map)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Request body must be an object with a 'subject' property containing all required fields.");
        }
        Map<String, Object> subject = (Map<String, Object>) payload.get("subject");

        // 2) Validate all required fields are present
        String[] required = new String[] {
                "year", "month", "day", "hour", "minute",
                "city", "name", "latitude", "longitude", "timezone"
        };
        for (String key : required) {
            if (!subject.containsKey(key) || subject.get(key) == null ||
                    (subject.get(key) instanceof String && ((String) subject.get(key)).isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field subject." + key);
            }
        }

        // 3) Extract & coerce types (accept number or numeric string)
        int year = asInt(subject.get("year"), "subject.year");
        int month = asInt(subject.get("month"), "subject.month");
        int day = asInt(subject.get("day"), "subject.day");
        int hour = asInt(subject.get("hour"), "subject.hour");
        int minute = asInt(subject.get("minute"), "subject.minute");

        String city = String.valueOf(subject.get("city"));
        String name = String.valueOf(subject.get("name"));
        double latitude = asDouble(subject.get("latitude"), "subject.latitude");
        double longitude = asDouble(subject.get("longitude"), "subject.longitude");
        String timezone = String.valueOf(subject.get("timezone"));

        // 4) Build DOB/TOB
        LocalDate dob = LocalDate.of(year, month, day);
        LocalTime tob = LocalTime.of(hour, minute);

        // 5) Load user and update stored fields to reflect the subject (keeps DB
        // consistent)
        UserProfile user = userService.getUser(id);
        user.setName(name); // optional, but keeps subject & user aligned
        user.setDateOfBirth(dob);
        user.setTimeOfBirth(tob);
        user.setPlaceOfBirth(city); // use 'city' to populate placeOfBirth
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setTimezone(timezone);

        // 6) Delegate to service to call RapidAPI and persist signs
        userService.updateSigns(user);

        // 7) Return the updated user
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates) {
        UserProfile updatedUser = userService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    // helpers
    private int asInt(Object val, String fieldName) {
        try {
            if (val instanceof Number n)
                return n.intValue();
            return Integer.parseInt(String.valueOf(val));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be an integer.");
        }
    }

    private double asDouble(Object val, String fieldName) {
        try {
            if (val instanceof Number n)
                return n.doubleValue();
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a number.");
        }
    }
}