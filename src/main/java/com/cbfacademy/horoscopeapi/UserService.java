package com.cbfacademy.horoscopeapi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
}