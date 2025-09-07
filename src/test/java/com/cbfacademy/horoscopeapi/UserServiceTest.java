package com.cbfacademy.horoscopeapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    UserProfileRepository userRepo;

    @Mock
    ZodiacSignRepository signRepo;

    @Mock
    HoroscopeService horoscopeService;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("createUser saves a new user with correct sun sign (calculated by DOB)")
    void createUser_savesWithDerivedSunSign() {

        LocalDate dob = LocalDate.parse("2024-04-01");
        LocalTime tob = LocalTime.of(10, 15);

        ZodiacSign aries = new ZodiacSign();
        aries.setName("Aries");
        when(signRepo.findByNameIgnoreCase("Aries")).thenReturn(Optional.of(aries));

        when(userRepo.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfile saved = userService.createUser("Shannon", dob, tob, "London");

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userRepo).save(captor.capture());
        UserProfile toSave = captor.getValue();

        assertEquals("Shannon", toSave.getName());
        assertEquals(dob, toSave.getDateOfBirth());
        assertEquals(tob, toSave.getTimeOfBirth());
        assertEquals("London", toSave.getPlaceOfBirth());
        assertNotNull(toSave.getSunSign());
        assertEquals("Aries", toSave.getSunSign().getName());

        assertEquals("Aries", saved.getSunSign().getName());
    }

    @Test
    @DisplayName("createUser throws exception when sun sign not found in database")
    void createUser_whenSignMissing_throws() {
        LocalDate dob = LocalDate.parse("2024-04-01");
        when(signRepo.findByNameIgnoreCase("Aries")).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.createUser("Shannon", dob, null, null));

        assertTrue(ex.getMessage().contains("Zodiac sign not found"), "Should mention missing sign");
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("updateSigns throws exception if time or place of birth is missing")
    void updateSigns_missingTimeOrPlace_throws() {

        UserProfile user = new UserProfile();
        user.setName("Shannon");
        user.setDateOfBirth(LocalDate.parse("1990-01-01"));
        user.setSunSign(new ZodiacSign());

        user.setLatitude(51.5);
        user.setLongitude(-0.12);
        user.setTimezone("Europe/London");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.updateSigns(user));

        assertTrue(ex.getMessage().contains("Time and place of birth must be set"));
        verifyNoInteractions(horoscopeService);
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("updateSigns throws exception if latitude/longitude/timezone is missing")
    void updateSigns_missingLatLonTz_throws() {
        UserProfile user = new UserProfile();
        user.setName("Shannon");
        user.setDateOfBirth(LocalDate.parse("1990-01-01"));
        user.setTimeOfBirth(LocalTime.of(8, 30));
        user.setPlaceOfBirth("London");
        user.setSunSign(new ZodiacSign());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.updateSigns(user));

        assertTrue(ex.getMessage().contains("Latitude, longitude, and timezone must be set"));
        verifyNoInteractions(horoscopeService);
        verify(userRepo, never()).save(any());
    }
}