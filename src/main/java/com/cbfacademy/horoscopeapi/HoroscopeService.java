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
        return null;
    }

    public String getDailyHoroscope(String sunSign) {
        return null;
    }

    public String getWeeklyHoroscope(String sunSign) {
        return null;
    }

    public String getMonthlyHoroscope(String sunSign) {
        return null;
    }
}