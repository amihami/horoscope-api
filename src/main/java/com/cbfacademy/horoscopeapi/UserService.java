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

        return null;
    }

    public UserProfile getUser(Long id) {
        return null;
    }

    public List<UserProfile> getAllUsers() {
        return null;
    }

    @Transactional
    public void deleteUser(Long id) {
    }
}