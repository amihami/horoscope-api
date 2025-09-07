package com.cbfacademy.horoscopeapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    // sun sign 
    List<UserProfile> findBySunSign_NameIgnoreCase(String sunSign);

    // moon sign 
    List<UserProfile> findByMoonSignIgnoreCase(String moonSign);

    // rising sign 
    List<UserProfile> findByRisingSignIgnoreCase(String risingSign);
}