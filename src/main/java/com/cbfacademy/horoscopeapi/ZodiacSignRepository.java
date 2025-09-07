package com.cbfacademy.horoscopeapi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ZodiacSignRepository extends JpaRepository<ZodiacSign, Integer> {
    Optional<ZodiacSign> findByNameIgnoreCase(String name);
}