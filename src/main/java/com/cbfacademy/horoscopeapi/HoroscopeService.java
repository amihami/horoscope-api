package com.cbfacademy.horoscopeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HoroscopeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://json.freeastrologyapi.com";
    private final String apiKey = "OlvnxEcksI2wMqHcnMbEr7OdU1bn03dM6R17GgUs";
    private final String horoscopeApiUrl = "https://horoscope-app-api.vercel.app/api/v1/get-horoscope";

    public HoroscopeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String fetchPublicHoroscope(String sign, String period, String day) {
        try {
            String url = horoscopeApiUrl + "/" + period + "?sign=" + sign.toLowerCase();
            if (day != null) {
                url += "&day=" + day.toLowerCase();
            }
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Horoscope-App-API error: " + e.getMessage(), e);
        }
    }

    public String getDailyHoroscope(String sunSign) {
        return fetchPublicHoroscope(sunSign, "daily", "today");
    }

    public String getWeeklyHoroscope(String sunSign) {
        return fetchPublicHoroscope(sunSign, "weekly", null);
    }

    public String getMonthlyHoroscope(String sunSign) {
        return fetchPublicHoroscope(sunSign, "monthly", null);
    }
}