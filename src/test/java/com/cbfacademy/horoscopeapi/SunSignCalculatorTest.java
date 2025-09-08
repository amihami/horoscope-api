package com.cbfacademy.horoscopeapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cbfacademy.horoscopeapi.util.SunSignCalculator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName(value = "SunSignCalculator")
class SunSignCalculatorTest {

        @Test
        @DisplayName(value = "All 12 signs are correct for midpoint dates")
        void signsCorrect() {
                assertEquals("Aries", SunSignCalculator.byDate(LocalDate.parse("2024-04-01")),
                                "Expected Aries for 2024-04-01");
                assertEquals("Taurus", SunSignCalculator.byDate(LocalDate.parse("2024-05-01")),
                                "Expected Taurus for 2024-05-01");
                assertEquals("Gemini", SunSignCalculator.byDate(LocalDate.parse("2024-06-01")),
                                "Expected Gemini for 2024-06-01");
                assertEquals("Cancer", SunSignCalculator.byDate(LocalDate.parse("2024-07-01")),
                                "Expected Cancer for 2024-07-01");
                assertEquals("Leo", SunSignCalculator.byDate(LocalDate.parse("2024-08-01")),
                                "Expected Leo for 2024-08-01");
                assertEquals("Virgo", SunSignCalculator.byDate(LocalDate.parse("2024-09-01")),
                                "Expected Virgo for 2024-09-01");
                assertEquals("Libra", SunSignCalculator.byDate(LocalDate.parse("2024-10-01")),
                                "Expected Libra for 2024-10-01");
                assertEquals("Scorpio", SunSignCalculator.byDate(LocalDate.parse("2024-11-01")),
                                "Expected Scorpio for 2024-11-01");
                assertEquals("Sagittarius", SunSignCalculator.byDate(LocalDate.parse("2024-12-01")),
                                "Expected Sagittarius for 2024-12-01");
                assertEquals("Capricorn", SunSignCalculator.byDate(LocalDate.parse("2024-01-10")),
                                "Expected Capricorn for 2024-01-10");
                assertEquals("Aquarius", SunSignCalculator.byDate(LocalDate.parse("2024-02-01")),
                                "Expected Aquarius for 2024-02-01");
                assertEquals("Pisces", SunSignCalculator.byDate(LocalDate.parse("2024-03-01")),
                                "Expected Pisces for 2024-03-01");
        }

        @Test
        @DisplayName(value = "Boundaries(edge cases), day before vs day of each transition")
        void boundaries() {
                // Pisces → Aries
                assertEquals("Pisces", SunSignCalculator.byDate(LocalDate.parse("2024-03-20")),
                                "Expected Pisces for 2024-03-20");
                assertEquals("Aries", SunSignCalculator.byDate(LocalDate.parse("2024-03-21")),
                                "Expected Aries for 2024-03-21");

                // Aries → Taurus
                assertEquals("Aries", SunSignCalculator.byDate(LocalDate.parse("2024-04-19")),
                                "Expected Aries for 2024-04-19");
                assertEquals("Taurus", SunSignCalculator.byDate(LocalDate.parse("2024-04-20")),
                                "Expected Taurus for 2024-04-20");

                // Taurus → Gemini
                assertEquals("Taurus", SunSignCalculator.byDate(LocalDate.parse("2024-05-20")),
                                "Expected Taurus for 2024-05-20");
                assertEquals("Gemini", SunSignCalculator.byDate(LocalDate.parse("2024-05-21")),
                                "Expected Gemini for 2024-05-21");

                // Gemini → Cancer
                assertEquals("Gemini", SunSignCalculator.byDate(LocalDate.parse("2024-06-20")),
                                "Expected Gemini for 2024-06-20");
                assertEquals("Cancer", SunSignCalculator.byDate(LocalDate.parse("2024-06-21")),
                                "Expected Cancer for 2024-06-21");

                // Cancer → Leo
                assertEquals("Cancer", SunSignCalculator.byDate(LocalDate.parse("2024-07-22")),
                                "Expected Cancer for 2024-07-22");
                assertEquals("Leo", SunSignCalculator.byDate(LocalDate.parse("2024-07-23")),
                                "Expected Leo for 2024-07-23");

                // Leo → Virgo
                assertEquals("Leo", SunSignCalculator.byDate(LocalDate.parse("2024-08-22")),
                                "Expected Leo for 2024-08-22");
                assertEquals("Virgo", SunSignCalculator.byDate(LocalDate.parse("2024-08-23")),
                                "Expected Virgo for 2024-08-23");

                // Virgo → Libra
                assertEquals("Virgo", SunSignCalculator.byDate(LocalDate.parse("2024-09-22")),
                                "Expected Virgo for 2024-09-22");
                assertEquals("Libra", SunSignCalculator.byDate(LocalDate.parse("2024-09-23")),
                                "Expected Libra for 2024-09-23");

                // Libra → Scorpio
                assertEquals("Libra", SunSignCalculator.byDate(LocalDate.parse("2024-10-22")),
                                "Expected Libra for 2024-10-22");
                assertEquals("Scorpio", SunSignCalculator.byDate(LocalDate.parse("2024-10-23")),
                                "Expected Scorpio for 2024-10-23");

                // Scorpio → Sagittarius
                assertEquals("Scorpio", SunSignCalculator.byDate(LocalDate.parse("2024-11-21")),
                                "Expected Scorpio for 2024-11-21");
                assertEquals("Sagittarius", SunSignCalculator.byDate(LocalDate.parse("2024-11-22")),
                                "Expected Sagittarius for 2024-11-22");

                // Sagittarius → Capricorn
                assertEquals("Sagittarius", SunSignCalculator.byDate(LocalDate.parse("2024-12-21")),
                                "Expected Sagittarius for 2024-12-21");
                assertEquals("Capricorn", SunSignCalculator.byDate(LocalDate.parse("2024-12-22")),
                                "Expected Capricorn for 2024-12-22");

                // Capricorn → Aquarius
                assertEquals("Capricorn", SunSignCalculator.byDate(LocalDate.parse("2024-01-19")),
                                "Expected Capricorn for 2024-01-19");
                assertEquals("Aquarius", SunSignCalculator.byDate(LocalDate.parse("2024-01-20")),
                                "Expected Aquarius for 2024-01-20");

                // Aquarius → Pisces
                assertEquals("Aquarius", SunSignCalculator.byDate(LocalDate.parse("2024-02-18")),
                                "Expected Aquarius for 2024-02-18");
                assertEquals("Pisces", SunSignCalculator.byDate(LocalDate.parse("2024-02-19")),
                                "Expected Pisces for 2024-02-19");
        }

        @Test
        @DisplayName(value = "Leap day (29 Feb) falls under Pisces")
        void leapDay() {
                assertEquals("Pisces", SunSignCalculator.byDate(LocalDate.parse("2020-02-29")),
                                "Expected Pisces for 2020-02-29");
        }
}