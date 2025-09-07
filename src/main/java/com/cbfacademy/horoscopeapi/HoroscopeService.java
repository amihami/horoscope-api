package com.cbfacademy.horoscopeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HoroscopeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Public horoscope API (kept)
    private final String horoscopeApiUrl = "https://horoscope-app-api.vercel.app/api/v1/get-horoscope";

    // RapidAPI Astrologer (names as requested)
    private final String rapidApiUrl  = "https://astrologer.p.rapidapi.com/api/v4/birth-chart";
    private final String rapidApiHost = "astrologer.p.rapidapi.com";
    private final String rapidApiKey  = "9a3e4f3829msh20a1322bdc9f34fp1790adjsneca176e1ef77";

    public HoroscopeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ----------------- Public horoscopes (unchanged) -----------------

    private String fetchPublicHoroscope(String sign, String period, String day) {
        try {
            String url = horoscopeApiUrl + "/" + period + "?sign=" + sign.toLowerCase();
            if (day != null) url += "&day=" + day.toLowerCase();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Horoscope-App-API error: " + e.getMessage(), e);
        }
    }

    public String getDailyHoroscope(String sunSign) { return fetchPublicHoroscope(sunSign, "daily", "today"); }
    public String getWeeklyHoroscope(String sunSign) { return fetchPublicHoroscope(sunSign, "weekly", null); }
    public String getMonthlyHoroscope(String sunSign) { return fetchPublicHoroscope(sunSign, "monthly", null); }

    // ----------------- Signs from RapidAPI -----------------

    /**
     * Builds EXACT payload:
     * {
     *   "subject": {
     *     "year","month","day","hour","minute",
     *     "city","name","latitude","longitude","timezone"
     *   }
     * }
     * and returns { sun, moon, rising } from the response.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getFullSigns(
            LocalDate dob,
            LocalTime tob,
            String name,
            String cityOrPlace,
            double latitude,
            double longitude,
            String timezone
    ) {
        try {
            if (dob == null) throw new IllegalArgumentException("Date of birth is required.");
            if (tob == null) throw new IllegalArgumentException("Time of birth is required.");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
            if (cityOrPlace == null || cityOrPlace.isBlank()) throw new IllegalArgumentException("City/place is required.");
            if (timezone == null || timezone.isBlank()) throw new IllegalArgumentException("Timezone is required.");

            // Build the EXACT subject body from existing fields (no extra DB columns needed)
            Map<String, Object> subject = new HashMap<>();
            subject.put("year",   dob.getYear());
            subject.put("month",  dob.getMonthValue());
            subject.put("day",    dob.getDayOfMonth());
            subject.put("hour",   tob.getHour());
            subject.put("minute", tob.getMinute());
            subject.put("city",   cityOrPlace);
            subject.put("name",   name);
            subject.put("latitude",  latitude);
            subject.put("longitude", longitude);
            subject.put("timezone",  timezone);

            Map<String, Object> body = new HashMap<>();
            body.put("subject", subject);

            // --- IMPORTANT: serialize to String to avoid "missing body" (no chunked) ---
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("x-rapidapi-host", rapidApiHost);
            headers.set("x-rapidapi-key",  rapidApiKey);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    rapidApiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Astrologer API error: HTTP " + response.getStatusCode());
            }

            Map<String, Object> root = objectMapper.readValue(response.getBody(), Map.class);
            Object dataObj = root.get("data");
            if (!(dataObj instanceof Map)) {
                throw new RuntimeException("Unexpected response: missing 'data' object");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;

            String sunSign    = extractSign(data.get("sun"));
            String moonSign   = extractSign(data.get("moon"));
            String risingSign = extractSign(data.get("asc")); // Ascendant

            Map<String, String> result = new HashMap<>();
            result.put("sun",    sunSign);
            result.put("moon",   moonSign);
            result.put("rising", risingSign);

            return result;

        } catch (RestClientResponseException re) {
            // Propagate server-provided error body for easier debugging
            String body = re.getResponseBodyAsString();
            throw new RuntimeException("API error: " + body, re);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate sun, moon, and rising signs", e);
        }
    }

    private String extractSign(Object planetOrPoint) {
        if (planetOrPoint instanceof Map<?, ?> m) {
            Object s = m.get("sign");
            return (s instanceof String) ? (String) s : null;
        }
        return null;
    }
}