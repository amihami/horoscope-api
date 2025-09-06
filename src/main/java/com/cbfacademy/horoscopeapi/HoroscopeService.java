package com.cbfacademy.horoscopeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

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

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

  
            Map<String, Object> parsed = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });

    
            return objectMapper.writeValueAsString(parsed);

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

            Map<String, Object> payload = new HashMap<>();
            payload.put("year", dob.getYear());
            payload.put("month", dob.getMonthValue());
            payload.put("date", dob.getDayOfMonth());
            payload.put("hours", tob.getHour());
            payload.put("minutes", tob.getMinute());
            payload.put("seconds", tob.getSecond());
            payload.put("latitude", latitude);
            payload.put("longitude", longitude);
            payload.put("timezone", timezone);

            Map<String, Object> config = new HashMap<>();
            config.put("observation_point", "topocentric");
            config.put("ayanamsha", "tropical");
            config.put("language", "en");

            payload.put("config", config);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String planetsUrl = baseUrl + "/western/planets";

            // delay for same reason as above
            Thread.sleep(1000);

            ResponseEntity<String> planetsResponse = restTemplate.postForEntity(planetsUrl, entity, String.class);

            Map<String, Object> planetsBody = objectMapper.readValue(
                    planetsResponse.getBody(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            List<Map<String, Object>> output = (List<Map<String, Object>>) planetsBody.get("output");

            String sunSign = null;
            String moonSign = null;
            String risingSign = null;

            for (Map<String, Object> planetEntry : output) {
                Map<String, Object> planet = (Map<String, Object>) planetEntry.get("planet");
                String planetName = (String) planet.get("en");

                Map<String, Object> zodiacSign = (Map<String, Object>) planetEntry.get("zodiac_sign");
                Map<String, String> nameMap = (Map<String, String>) zodiacSign.get("name");
                String signName = nameMap.get("en");

                switch (planetName.toLowerCase()) {
                    case "sun" -> sunSign = signName;
                    case "moon" -> moonSign = signName;
                    case "ascendant" -> risingSign = signName;
                }
            }

            Map<String, String> result = new HashMap<>();
            result.put("sun", sunSign);
            result.put("moon", moonSign);
            result.put("rising", risingSign);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate sun, moon, and rising signs", e);
        }
    }
}