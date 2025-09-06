package com.cbfacademy.horoscopeapi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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

    public UserProfile getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
    }

    public List<UserProfile> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
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

        // Call HoroscopeService to get full signs (sun, moon, rising)
        Map<String, String> signs = horoscopeService.getFullSigns(
                user.getDateOfBirth(),
                user.getTimeOfBirth(),
                user.getPlaceOfBirth());

        // Update sun sign from API response, preferred if available
        String sunSignName = signs.get("sun");
        if (sunSignName == null || sunSignName.isEmpty()) {
            // fallback to calculation by date
            sunSignName = SunSignCalculator.byDate(user.getDateOfBirth());
        }
        ZodiacSign sunSign = signRepo.findByNameIgnoreCase(sunSignName)
                .orElseThrow(() -> new IllegalStateException("Sun sign not found in DB"));
        user.setSunSign(sunSign);

        // Update moon and rising signs from API
        user.setMoonSign(signs.get("moon"));
        user.setRisingSign(signs.get("rising"));

        userRepo.save(user);
    }

    @Transactional
    public UserProfile updateUser(Long id, Map<String, String> updates) {
        // Get user from the database
        UserProfile user = getUser(id);

        // Update fields if present in the request map
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

        // If dateOfBirth or placeOfBirth changed, recalculate sugn sign
        if (updates.containsKey("dateOfBirth") || updates.containsKey("placeOfBirth")) {
            String sunSignName = SunSignCalculator.byDate(user.getDateOfBirth());
            ZodiacSign sunSign = signRepo.findByNameIgnoreCase(sunSignName)
                    .orElseThrow(() -> new IllegalStateException("Sun sign not found in DB"));
            user.setSunSign(sunSign);
        }
        return user;
    }

}