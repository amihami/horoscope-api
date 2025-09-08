package com.cbfacademy.horoscopeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cbfacademy.horoscopeapi.model.ZodiacSign;

import java.util.Optional;

public interface ZodiacSignRepository extends JpaRepository<ZodiacSign, Integer> {
    Optional<ZodiacSign> findByNameIgnoreCase(String name);
}