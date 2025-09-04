package com.cbfacademy.horoscopeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


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

    @SuppressWarnings("unchecked")
    public Map<String, String> getFullSigns(LocalDate dob, LocalTime tob, String place) {
        try {
            String geoUrl = baseUrl + "/geo-details";
            Map<String, String> geoPayload = new HashMap<>();
            geoPayload.put("location", place);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);

            HttpEntity<Map<String, String>> geoEntity = new HttpEntity<>(geoPayload, headers);

            // Delay 1 second before request to accomodate API access restrictions
            Thread.sleep(1000);

            ResponseEntity<String> geoResponse = restTemplate.postForEntity(geoUrl, geoEntity, String.class);

            // Handle both single object and list responses
            Object geoResponseObj = objectMapper.readValue(geoResponse.getBody(), Object.class);
            Map<String, Object> geo;

            if (geoResponseObj instanceof List) {
                List<Map<String, Object>> geoList = (List<Map<String, Object>>) geoResponseObj;
                geo = geoList.get(0); 
            } else if (geoResponseObj instanceof Map) {
                geo = (Map<String, Object>) geoResponseObj;
            } else {
                throw new RuntimeException("Unexpected geo-details response format");
            }

            if (geo.get("latitude") == null || geo.get("longitude") == null || geo.get("timezone_offset") == null) {
                throw new RuntimeException("Geo-details API returned incomplete location info for: " + place);
            }

            double latitude = ((Number) geo.get("latitude")).doubleValue();
            double longitude = ((Number) geo.get("longitude")).doubleValue();
            double timezone = ((Number) geo.get("timezone_offset")).doubleValue();

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate sun, moon, and rising signs", e);
        }
    }
}