package com.cbfacademy.horoscopeapi;

import com.cbfacademy.horoscopeapi.dto.CalculateSignsRequest;
import com.cbfacademy.horoscopeapi.dto.CreateUserRequest;
import com.cbfacademy.horoscopeapi.dto.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final HoroscopeService horoscopeService;

    public UserController(UserService userService, HoroscopeService horoscopeService) {
        this.userService = userService;
        this.horoscopeService = horoscopeService;
    }

    @Operation(summary = "Create user", description = "Creates a new user. If time/place are absent, only the Sun sign is set (by DOB).", tags = "1. User Management")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserRequest.class), examples = @ExampleObject(name = "Basic", value = """
            {
              "name": "Shannon",
              "dateOfBirth": "1990-01-01",
              "timeOfBirth": "08:30",
              "placeOfBirth": "London"
            }
            """)))
    @ApiResponse(responseCode = "201", description = "User created", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> createUser(
            @org.springframework.web.bind.annotation.RequestBody CreateUserRequest body) {

        if (body == null || body.name == null || body.name.isBlank() || body.dateOfBirth == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name and dateOfBirth are required.");
        }

        LocalDate dob = LocalDate.parse(body.dateOfBirth);
        LocalTime timeOfBirth = (body.timeOfBirth == null || body.timeOfBirth.isBlank())
                ? null
                : LocalTime.parse(body.timeOfBirth);
        String placeOfBirth = (body.placeOfBirth == null || body.placeOfBirth.isBlank())
                ? null
                : body.placeOfBirth;

        UserProfile user = userService.createUser(body.name, dob, timeOfBirth, placeOfBirth);
        return ResponseEntity.created(URI.create("/api/users/" + user.getId())).body(user);
    }

    @Operation(summary = "Get user by id", tags = "1. User Management")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public UserProfile getUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id) {
        return userService.getUser(id);
    }

    @Operation(summary = "List users", tags = "1. User Management")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Update user", description = "Partial update. Only provided fields are applied.", tags = "1. User Management")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateUserRequest.class), examples = @ExampleObject(name = "Partial update", value = """
            {
              "placeOfBirth": "Manchester",
              "latitude": "53.4808",
              "longitude": "-2.2426",
              "timezone": "Europe/London"
            }
            """)))
    @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> updateUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody UpdateUserRequest updates) {

        Map<String, String> map = new HashMap<>();
        if (updates.name != null)
            map.put("name", updates.name);
        if (updates.dateOfBirth != null)
            map.put("dateOfBirth", updates.dateOfBirth);
        if (updates.timeOfBirth != null)
            map.put("timeOfBirth", updates.timeOfBirth);
        if (updates.placeOfBirth != null)
            map.put("placeOfBirth", updates.placeOfBirth);
        if (updates.latitude != null)
            map.put("latitude", updates.latitude);
        if (updates.longitude != null)
            map.put("longitude", updates.longitude);
        if (updates.timezone != null)
            map.put("timezone", updates.timezone);

        UserProfile updatedUser = userService.updateUser(id, map);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user", tags = "1. User Management")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Daily forecast (by user's sun sign)", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/{id}/horoscope/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDailyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getDailyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @Operation(summary = "Weekly forecast (by user's sun sign)", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/{id}/horoscope/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeeklyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getWeeklyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @Operation(summary = "Monthly forecast (by user's sun sign)", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/{id}/horoscope/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMonthlyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String horoscope = horoscopeService.getMonthlyHoroscope(sunSign);
        return ResponseEntity.ok(horoscope);
    }

    @Operation(summary = "Calculate signs", description = "Calculates and stores sun, moon, rising. Strict subject payload.", tags = "3. Calculate Signs")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculateSignsRequest.class), examples = @ExampleObject(name = "Strict subject", value = """
            {
              "subject": {
                "year": 1990,
                "month": 1,
                "day": 1,
                "hour": 8,
                "minute": 30,
                "city": "London",
                "name": "Shannon",
                "latitude": 51.5072,
                "longitude": -0.1276,
                "timezone": "Europe/London"
              }
            }
            """)))
    @ApiResponse(responseCode = "200", description = "User updated with signs", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @ApiResponse(responseCode = "400", description = "Invalid or missing fields", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/{id}/calculate-signs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> calculateSigns(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody CalculateSignsRequest payload) {

        if (payload == null || payload.subject == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Request body must include 'subject' with all required fields.");
        }
        var s = payload.subject;

        String[] missing = requiredMissing(
                Map.of(
                        "subject.year", s.year,
                        "subject.month", s.month,
                        "subject.day", s.day,
                        "subject.hour", s.hour,
                        "subject.minute", s.minute,
                        "subject.city", s.city,
                        "subject.name", s.name,
                        "subject.latitude", s.latitude,
                        "subject.longitude", s.longitude,
                        "subject.timezone", s.timezone));
        if (missing.length > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Missing required field(s): " + String.join(", ", missing));
        }

        LocalDate dob = LocalDate.of(s.year, s.month, s.day);
        LocalTime tob = LocalTime.of(s.hour, s.minute);

        UserProfile user = userService.getUser(id);
        user.setName(s.name);
        user.setDateOfBirth(dob);
        user.setTimeOfBirth(tob);
        user.setPlaceOfBirth(s.city);
        user.setLatitude(s.latitude);
        user.setLongitude(s.longitude);
        user.setTimezone(s.timezone);

        userService.updateSigns(user);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Find users by Sun sign", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @GetMapping("/by-sun")
    public List<UserProfile> findBySun(
            @Parameter(description = "Sun sign name, e.g. 'Aries'", required = true, example = "Aries") @RequestParam("sign") String sign) {
        return userService.findBySunSign(sign);
    }

    @Operation(summary = "Find users by Moon sign", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @GetMapping("/by-moon")
    public List<UserProfile> findByMoon(
            @Parameter(description = "Moon sign name, e.g. 'Cancer'", required = true, example = "Cancer") @RequestParam("sign") String sign) {
        return userService.findByMoonSign(sign);
    }

    @Operation(summary = "Find users by Rising sign", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserProfile.class)))
    @GetMapping("/by-rising")
    public List<UserProfile> findByRising(
            @Parameter(description = "Rising sign name, e.g. 'Libra'", required = true, example = "Libra") @RequestParam("sign") String sign) {
        return userService.findByRisingSign(sign);
    }

    private String[] requiredMissing(Map<String, ?> fields) {
        List<String> missing = new ArrayList<>();
        fields.forEach((k, v) -> {
            if (v == null || (v instanceof String s && s.isBlank()))
                missing.add(k);
        });
        return missing.toArray(String[]::new);
    }
}