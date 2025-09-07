package com.cbfacademy.horoscopeapi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserProfileRepository userRepo;
    private final ZodiacSignRepository signRepo;
    private final HoroscopeService horoscopeService;

    public UserService(UserProfileRepository userRepo, ZodiacSignRepository signRepo,
                       HoroscopeService horoscopeService) {
        this.userRepo = userRepo;
        this.signRepo = signRepo;
        this.horoscopeService = horoscopeService;
    }

    @Transactional
    public UserProfile createUser(String name, LocalDate dob, LocalTime timeOfBirth, String placeOfBirth) {

        String sunSignName = SunSignCalculator.byDate(dob);
        ZodiacSign sunSign = signRepo.findByNameIgnoreCase(sunSignName)
                .orElseThrow(() -> new IllegalStateException("Zodiac sign not found: " + sunSignName));

        UserProfile user = new UserProfile();
        user.setName(name);
        user.setDateOfBirth(dob);
        user.setTimeOfBirth(timeOfBirth);
        user.setPlaceOfBirth(placeOfBirth);
        user.setSunSign(sunSign);

        return userRepo.save(user);
    }

    public UserProfile getUser(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
    }

    public List<UserProfile> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User not found with id " + id);
        }
        userRepo.deleteById(id);
    }

    @Transactional
    public void updateSigns(UserProfile user) {
        if (user.getTimeOfBirth() == null || user.getPlaceOfBirth() == null) {
            throw new IllegalArgumentException("Time and place of birth must be set to calculate signs.");
        }
        if (user.getLatitude() == null || user.getLongitude() == null ||
            user.getTimezone() == null || user.getTimezone().isBlank()) {
            throw new IllegalArgumentException("Latitude, longitude, and timezone must be set to calculate signs.");
        }

        // Use placeOfBirth for the "city" field in the RapidAPI payload
        String cityOrPlace = user.getPlaceOfBirth();

        Map<String, String> signs = horoscopeService.getFullSigns(
                user.getDateOfBirth(),
                user.getTimeOfBirth(),
                user.getName(),
                cityOrPlace,
                user.getLatitude(),
                user.getLongitude(),
                user.getTimezone()
        );

        // Normalize sun sign from API (may be "Ari", "Sag", etc.) to full name for DB lookup
        String sunFromApi = signs.get("sun");
        String sunSignFull = toFullSunSign(sunFromApi);

        if (sunSignFull == null || sunSignFull.isBlank()) {
            // Fallback to date-based if API unexpected
            sunSignFull = SunSignCalculator.byDate(user.getDateOfBirth());
        }

        ZodiacSign sunSign = signRepo.findByNameIgnoreCase(sunSignFull)
                .orElseThrow(() -> new IllegalStateException("Sun sign not found in DB"));

        user.setSunSign(sunSign);

        // Keep moon/rising as the short codes (matches your example output)
        user.setMoonSign(signs.get("moon"));
        user.setRisingSign(signs.get("rising"));

        userRepo.save(user);
    }

    @Transactional
    public UserProfile updateUser(UUID id, Map<String, String> updates) {
        UserProfile user = getUser(id);

        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        if (updates.containsKey("dateOfBirth")) {
            user.setDateOfBirth(LocalDate.parse(updates.get("dateOfBirth")));
        }
        if (updates.containsKey("timeOfBirth")) {
            user.setTimeOfBirth(LocalTime.parse(updates.get("timeOfBirth")));
        }
        if (updates.containsKey("placeOfBirth")) {
            user.setPlaceOfBirth(updates.get("placeOfBirth"));
        }
        if (updates.containsKey("latitude")) {
            user.setLatitude(Double.parseDouble(updates.get("latitude")));
        }
        if (updates.containsKey("longitude")) {
            user.setLongitude(Double.parseDouble(updates.get("longitude")));
        }
        if (updates.containsKey("timezone")) {
            user.setTimezone(updates.get("timezone"));
        }

        if (updates.containsKey("dateOfBirth") || updates.containsKey("placeOfBirth")) {
            String sunSignName = SunSignCalculator.byDate(user.getDateOfBirth());
            ZodiacSign sunSign = signRepo.findByNameIgnoreCase(sunSignName)
                    .orElseThrow(() -> new IllegalStateException("Sun sign not found in DB"));
            user.setSunSign(sunSign);
        }

        return user;
    }

    // --- Helpers ---

    private String toFullSunSign(String sign) {
        if (sign == null) return null;
        String s = sign.trim();
        if (s.isEmpty()) return null;

        // Already full name?
        switch (s.toLowerCase()) {
            case "aries": return "Aries";
            case "taurus": return "Taurus";
            case "gemini": return "Gemini";
            case "cancer": return "Cancer";
            case "leo": return "Leo";
            case "virgo": return "Virgo";
            case "libra": return "Libra";
            case "scorpio": return "Scorpio";
            case "sagittarius": return "Sagittarius";
            case "capricorn": return "Capricorn";
            case "aquarius": return "Aquarius";
            case "pisces": return "Pisces";
        }

        // Common 3-letter codes from the API
        String abbr = s.length() >= 3 ? s.substring(0, 3).toLowerCase() : s.toLowerCase();
        switch (abbr) {
            case "ari": return "Aries";
            case "tau": return "Taurus";
            case "gem": return "Gemini";
            case "can": return "Cancer";
            case "leo": return "Leo";
            case "vir": return "Virgo";
            case "lib": return "Libra";
            case "sco": return "Scorpio";
            case "sag": return "Sagittarius";
            case "cap": return "Capricorn";
            case "aqu": return "Aquarius";
            case "pis": return "Pisces";
            default: return null;
        }
    }
}