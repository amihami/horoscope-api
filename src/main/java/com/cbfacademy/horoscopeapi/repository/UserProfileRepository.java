package com.cbfacademy.horoscopeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cbfacademy.horoscopeapi.model.UserProfile;

import java.util.List;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    List<UserProfile> findBySunSign_NameIgnoreCase(String sunSign);

    List<UserProfile> findByMoonSignIgnoreCase(String moonSign);

    List<UserProfile> findByRisingSignIgnoreCase(String risingSign);
}