package com.cbfacademy.horoscopeapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class HoroscopeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String horoscopeApiUrl = "https://horoscope-app-api.vercel.app/api/v1/get-horoscope";

    private final String rapidApiUrl = "https://astrologer.p.rapidapi.com/api/v4/birth-chart";
    private final String rapidApiHost = "astrologer.p.rapidapi.com";
    private final String rapidApiKey = "9a3e4f3829msh20a1322bdc9f34fp1790adjsneca176e1ef77";

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
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            Object dataObj = parsed.get("data");
            if (dataObj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) dataObj;

                Object textObj = data.get("horoscope_data");
                if (textObj instanceof String) {
                    data.put("horoscope_data", cleanHoroscopeText((String) textObj));
                }
            }

            return objectMapper.writeValueAsString(parsed);

        } catch (Exception e) {
            throw new RuntimeException("Horoscope-App-API error: " + e.getMessage(), e);
        }
    }

    private String cleanHoroscopeText(String originalText) {
        if (originalText == null)
            return null;

        String cleanedText = Normalizer.normalize(originalText, Normalizer.Form.NFKD);

        cleanedText = cleanedText.replace("\uFB01", "fi")
                .replace("\uFB02", "fl");

        cleanedText = cleanedText.replace("\u2018", "'")
                .replace("\u2019", "'")
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u2013", "-")
                .replace("\u2014", "-")
                .replace("\u2026", "...")
                .replace('\u00A0', ' ');

        cleanedText = cleanedText.replaceAll("(?i)re\\?nement", "refinement")
                .replaceAll("(?i)re\\?ning", "refining")
                .replaceAll("(?i)re-?de\\?ning", "re-defining")
                .replaceAll("(?i)de\\?ning", "defining")
                .replaceAll("(?i)de\\?ne", "define")
                .replaceAll("(?i)re\\?ne", "refine")
                .replaceAll("(?i)in\\?uence", "influence");

        return cleanedText;
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

    public Map<String, String> getFullSigns(
            LocalDate dob,
            LocalTime tob,
            String name,
            String cityOrPlace,
            double latitude,
            double longitude,
            String timezone) {
        try {
            if (dob == null)
                throw new IllegalArgumentException("Date of birth is required.");
            if (tob == null)
                throw new IllegalArgumentException("Time of birth is required.");
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Name is required.");
            if (cityOrPlace == null || cityOrPlace.isBlank())
                throw new IllegalArgumentException("City/place is required.");
            if (timezone == null || timezone.isBlank())
                throw new IllegalArgumentException("Timezone is required.");

            Map<String, Object> subject = new HashMap<>();
            subject.put("year", dob.getYear());
            subject.put("month", dob.getMonthValue());
            subject.put("day", dob.getDayOfMonth());
            subject.put("hour", tob.getHour());
            subject.put("minute", tob.getMinute());
            subject.put("city", cityOrPlace);
            subject.put("name", name);
            subject.put("latitude", latitude);
            subject.put("longitude", longitude);
            subject.put("timezone", timezone);

            Map<String, Object> body = new HashMap<>();
            body.put("subject", subject);

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("x-rapidapi-host", rapidApiHost);
            headers.set("x-rapidapi-key", rapidApiKey);

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

            String sunSign = extractSign(data.get("sun"));
            String moonSign = extractSign(data.get("moon"));
            String risingSign = extractRisingSign(data);

            Map<String, String> result = new HashMap<>();
            result.put("sun", sunSign);
            result.put("moon", moonSign);
            result.put("rising", risingSign);

            return result;

        } catch (RestClientResponseException re) {
            String body = re.getResponseBodyAsString();
            throw new RuntimeException("API error: " + body, re);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate sun, moon, and rising signs", e);
        }
    }

    private String extractSign(Object planetOrPoint) {
        if (planetOrPoint instanceof Map<?, ?> m) {
            Object s = m.get("sign");
            if (s instanceof String)
                return (String) s;

            Object zs = m.get("zodiac_sign");
            if (zs instanceof Map<?, ?> zsm) {
                Object sign = zsm.get("sign");
                if (sign instanceof String)
                    return (String) sign;
                Object name = zsm.get("name");
                if (name instanceof Map<?, ?> nm) {
                    Object en = nm.get("en");
                    if (en instanceof String)
                        return abbrev((String) en);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractRisingSign(Map<String, Object> data) {

        String s1 = extractSign(data.get("asc"));
        if (s1 != null)
            return s1;

        String s2 = extractSign(data.get("ascendant"));
        if (s2 != null)
            return s2;

        String s3 = extractSign(data.get("first_house"));
        if (s3 != null)
            return s3;

        Object houses = data.get("houses");
        if (houses instanceof List<?> list) {
            for (Object h : list) {
                if (h instanceof Map<?, ?> hm) {

                    Object num = hm.get("number");
                    if (num instanceof Number && ((Number) num).intValue() == 1) {
                        String sign = extractSign(hm);
                        if (sign != null)
                            return sign;
                    }
                    Object name = hm.get("name");
                    if (name instanceof String && ((String) name).equalsIgnoreCase("first_house")) {
                        String sign = extractSign(hm);
                        if (sign != null)
                            return sign;
                    }
                }
            }
        } else if (houses instanceof Map<?, ?> hm) {
            Object first = hm.get("first_house");
            String sign = extractSign(first);
            if (sign != null)
                return sign;
        }

        return null;
    }

    private String abbrev(String full) {
        if (full == null)
            return null;
        switch (full.trim().toLowerCase()) {
            case "aries":
                return "Ari";
            case "taurus":
                return "Tau";
            case "gemini":
                return "Gem";
            case "cancer":
                return "Can";
            case "leo":
                return "Leo";
            case "virgo":
                return "Vir";
            case "libra":
                return "Lib";
            case "scorpio":
                return "Sco";
            case "sagittarius":
                return "Sag";
            case "capricorn":
                return "Cap";
            case "aquarius":
                return "Aqu";
            case "pisces":
                return "Pis";
            default:
                return full;
        }
    }
}