package com.cbfacademy.horoscopeapi;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "time_of_birth")
    private LocalTime timeOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    private Double latitude;
    private Double longitude;
    private String timezone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sun_sign_id")
    private ZodiacSign sunSign;

    @Column(name = "rising_sign")
    private String risingSign;

    @Column(name = "moon_sign")
    private String moonSign;

    public UserProfile() {
    }

    public UserProfile(String name, LocalDate dateOfBirth, ZodiacSign sunSign) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.sunSign = sunSign;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalTime getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfBirth(LocalTime timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public ZodiacSign getSunSign() {
        return sunSign;
    }

    public void setSunSign(ZodiacSign sunSign) {
        this.sunSign = sunSign;
    }

    public String getRisingSign() {
        return risingSign;
    }

    public void setRisingSign(String risingSign) {
        this.risingSign = risingSign;
    }

    public String getMoonSign() {
        return moonSign;
    }

    public void setMoonSign(String moonSign) {
        this.moonSign = moonSign;
    }
}
