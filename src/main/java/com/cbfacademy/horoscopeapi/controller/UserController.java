package com.cbfacademy.horoscopeapi.controller;

import com.cbfacademy.horoscopeapi.dto.CalculateSignsRequest;
import com.cbfacademy.horoscopeapi.dto.CreateUserRequest;
import com.cbfacademy.horoscopeapi.dto.HoroscopeView;
import com.cbfacademy.horoscopeapi.dto.UpdateUserRequest;
import com.cbfacademy.horoscopeapi.model.UserProfile;
import com.cbfacademy.horoscopeapi.service.HoroscopeService;
import com.cbfacademy.horoscopeapi.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
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

    private final ObjectMapper om = new ObjectMapper();

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
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> createUser(@RequestBody CreateUserRequest body) {

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
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}")
    public UserProfile getUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id) {
        return userService.getUser(id);
    }

    @Operation(summary = "List users", tags = "1. User Management")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserProfile.class))))
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
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> updateUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id,
            @RequestBody UpdateUserRequest updates) {

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
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(in = ParameterIn.PATH, description = "User id", required = true) @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Daily forecast (by user's sun sign)", description = "Returns a simplified object with sign, period (daily), day (today) and cleaned horoscope text.", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "Horoscope retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HoroscopeView.class), examples = @ExampleObject(name = "DailyExample", value = "{\n  \"sign\": \"Aries\",\n  \"period\": \"daily\",\n  \"day\": \"today\",\n  \"text\": \"Momentum builds as you take initiative today...\"\n}")))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(value = "/{id}/horoscope/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HoroscopeView> getDailyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String json = horoscopeService.getDailyHoroscope(sunSign);
        HoroscopeView view = toHoroscopeView(json, "daily", "today");
        return ResponseEntity.ok(view);
    }

    @Operation(summary = "Weekly forecast (by user's sun sign)", description = "Returns a simplified object with sign, period (weekly) and cleaned horoscope text.", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "Horoscope retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HoroscopeView.class), examples = @ExampleObject(name = "WeeklyExample", value = "{\n  \"sign\": \"Aries\",\n  \"period\": \"weekly\",\n  \"day\": null,\n  \"text\": \"This week favors decisive moves and collaboration...\"\n}")))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(value = "/{id}/horoscope/weekly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HoroscopeView> getWeeklyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String json = horoscopeService.getWeeklyHoroscope(sunSign);
        HoroscopeView view = toHoroscopeView(json, "weekly", null);
        return ResponseEntity.ok(view);
    }

    @Operation(summary = "Monthly forecast (by user's sun sign)", description = "Returns a simplified object with sign, period (monthly) and cleaned horoscope text.", tags = "2. Forecasting")
    @ApiResponse(responseCode = "200", description = "Horoscope retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HoroscopeView.class), examples = @ExampleObject(name = "MonthlyExample", value = "{\n  \"sign\": \"Aries\",\n  \"period\": \"monthly\",\n  \"day\": null,\n  \"text\": \"A fresh cycle brings new ambitions—pace yourself and plan...\"\n}")))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(value = "/{id}/horoscope/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HoroscopeView> getMonthlyHoroscope(@PathVariable UUID id) {
        UserProfile user = userService.getUser(id);
        String sunSign = user.getSunSign().getName();
        String json = horoscopeService.getMonthlyHoroscope(sunSign);
        HoroscopeView view = toHoroscopeView(json, "monthly", null);
        return ResponseEntity.ok(view);
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
    @ApiResponse(responseCode = "400", description = "Invalid or missing fields", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping(value = "/{id}/calculate-signs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> calculateSigns(
            @PathVariable UUID id,
            @RequestBody CalculateSignsRequest payload) {

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


    @Operation(summary = "Find users by Sun sign", description = "Case-insensitive exact match on stored Sun sign.", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserProfile.class)), examples = @ExampleObject(name = "SunSignExample", value = "[{\n  \"id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n  \"name\":\"Shannon\",\n  \"dateOfBirth\":\"1990-01-01\",\n  \"timeOfBirth\": \"08:30\",\n  \"placeOfBirth\":\"London\",\n  \"latitude\":51.5072,\n  \"longitude\":-0.1276,\n  \"timezone\":\"Europe/London\",\n  \"sunSign\":{\"id\":1,\"name\":\"Aries\",\"element\":\"Fire\",\"modality\":\"Cardinal\",\"rulingPlanet\":\"Mars\",\"traits\":\"Bold, energetic\"},\n  \"risingSign\":\"Libra\",\n  \"moonSign\":\"Cancer\"\n}]")))
    @ApiResponse(responseCode = "400", description = "Missing or invalid 'sign' parameter", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/by-sun")
    public List<UserProfile> findBySun(
            @Parameter(in = ParameterIn.QUERY, description = "Sun sign name, e.g. 'Aries'", required = true, example = "Aries") @RequestParam("sign") String sign) {
        if (sign == null || sign.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'sign' is required.");
        }
        return userService.findBySunSign(sign);
    }

    @Operation(summary = "Find users by Moon sign", description = "Case-insensitive exact match on stored Moon sign.", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserProfile.class)), examples = @ExampleObject(name = "MoonSignExample", value = "[{\n  \"id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n  \"name\":\"Shannon\",\n  \"dateOfBirth\":\"1990-01-01\",\n  \"moonSign\":\"Cancer\"\n}]")))
    @ApiResponse(responseCode = "400", description = "Missing or invalid 'sign' parameter", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/by-moon")
    public List<UserProfile> findByMoon(
            @Parameter(in = ParameterIn.QUERY, description = "Moon sign name, e.g. 'Cancer'", required = true, example = "Cancer") @RequestParam("sign") String sign) {
        if (sign == null || sign.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'sign' is required.");
        }
        return userService.findByMoonSign(sign);
    }

    @Operation(summary = "Find users by Rising sign", description = "Case-insensitive exact match on stored Rising sign.", tags = "4. Find User By Sign")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserProfile.class)), examples = @ExampleObject(name = "RisingSignExample", value = "[{\n  \"id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n  \"name\":\"Shannon\",\n  \"dateOfBirth\":\"1990-01-01\",\n  \"risingSign\":\"Libra\"\n}]")))
    @ApiResponse(responseCode = "400", description = "Missing or invalid 'sign' parameter", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/by-rising")
    public List<UserProfile> findByRising(
            @Parameter(in = ParameterIn.QUERY, description = "Rising sign name, e.g. 'Libra'", required = true, example = "Libra") @RequestParam("sign") String sign) {
        if (sign == null || sign.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'sign' is required.");
        }
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

    private HoroscopeView toHoroscopeView(String upstreamJson, String period, String day) {
        try {
            Map<String, Object> root = om.readValue(upstreamJson, new TypeReference<Map<String, Object>>() {
            });
            Object dataObj = root.get("data");
            String sign = null;
            String text = null;

            if (dataObj instanceof Map<?, ?> data) {
                Object s = ((Map<String, Object>) data).get("sign");
                if (s instanceof String) {
                    sign = capitalizeFirst((String) s);
                }
                Object t = ((Map<String, Object>) data).get("horoscope_data");
                if (t != null) {
                    text = String.valueOf(t);
                }
            }

            HoroscopeView v = new HoroscopeView();
            v.setSign(sign != null ? sign : "Unknown");
            v.setPeriod(period);
            v.setDay(day);
            v.setText(text != null ? text : "");
            return v;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to parse upstream horoscope.", e);
        }
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isBlank())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}