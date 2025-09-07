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

/**
 * Pure unit tests for UserService using Mockito.
 */
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
        // Arrange
        LocalDate dob = LocalDate.parse("2024-04-01"); // Aries by sunSignCalculator
        LocalTime tob = LocalTime.of(10, 15);

        ZodiacSign aries = new ZodiacSign();
        aries.setName("Aries");
        when(signRepo.findByNameIgnoreCase("Aries")).thenReturn(Optional.of(aries));

        // Return the same instance that was passed 
        when(userRepo.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UserProfile saved = userService.createUser("Shannon", dob, tob, "London");

        // Assert to check the entity has passed to save
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userRepo).save(captor.capture());
        UserProfile toSave = captor.getValue();

        assertEquals("Shannon", toSave.getName());
        assertEquals(dob, toSave.getDateOfBirth());
        assertEquals(tob, toSave.getTimeOfBirth());
        assertEquals("London", toSave.getPlaceOfBirth());
        assertNotNull(toSave.getSunSign());
        assertEquals("Aries", toSave.getSunSign().getName());

        // the returned value mirrors what was saved
        assertEquals("Aries", saved.getSunSign().getName());
    }

    @Test
    @DisplayName("createUser throws exception when sun sign not found in database")
    void createUser_whenSignMissing_throws() {
        // Arrange
        LocalDate dob = LocalDate.parse("2024-04-01"); // Aries expected
        when(signRepo.findByNameIgnoreCase("Aries")).thenReturn(Optional.empty());

        // Act + Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                userService.createUser("Shannon", dob, null, null));

        assertTrue(ex.getMessage().contains("Zodiac sign not found"), "Should mention missing sign");
        verify(userRepo, never()).save(any()); // nothing saved
    }

    @Test
    @DisplayName("updateSigns throws exception if time or place of birth is missing")
    void updateSigns_missingTimeOrPlace_throws() {
        // Arrange: user missing time/place
        UserProfile user = new UserProfile();
        user.setName("Shannon");
        user.setDateOfBirth(LocalDate.parse("1990-01-01"));
        user.setSunSign(new ZodiacSign()); // not relevant

        // Just latitude/longitude/timezone set so we hit the first guard
        user.setLatitude(51.5);
        user.setLongitude(-0.12);
        user.setTimezone("Europe/London");

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.updateSigns(user));

        assertTrue(ex.getMessage().contains("Time and place of birth must be set"));
        verifyNoInteractions(horoscopeService);
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("updateSigns throws exception if latitude/longitude/timezone is missing")
    void updateSigns_missingLatLonTz_throws() {
        // Arrange: user has time/place but missing coords/tz
        UserProfile user = new UserProfile();
        user.setName("Shannon");
        user.setDateOfBirth(LocalDate.parse("1990-01-01"));
        user.setTimeOfBirth(LocalTime.of(8, 30));
        user.setPlaceOfBirth("London");
        user.setSunSign(new ZodiacSign());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.updateSigns(user));

        assertTrue(ex.getMessage().contains("Latitude, longitude, and timezone must be set"));
        verifyNoInteractions(horoscopeService);
        verify(userRepo, never()).save(any());
    }
}