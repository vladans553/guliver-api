package com.guliver.api.service;




import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ElevationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getPlaces(String place) {

        try {
            // =========================
            // 1. GEOCODING (Nominatim)
            // =========================
            String geoUrl = "https://nominatim.openstreetmap.org/search?q="
                    + place + "&format=json&limit=5";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "ElevationCalculator/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> geoResponse =
                    restTemplate.exchange(geoUrl, HttpMethod.GET, entity, List.class);

            List<Map<String, Object>> geoList = geoResponse.getBody();

            if (geoList == null || geoList.isEmpty()) {
                return Map.of("error", "No locations found");
            }

            // =========================
            // 2. MAPIRANJE REZULTATA
            // =========================
            List<Map<String, Object>> results = new ArrayList<>();

            for (Object obj : geoList) {
                Map item = (Map) obj;

                Map<String, Object> simplified = new HashMap<>();
                simplified.put("name", item.get("display_name"));
                simplified.put("lat", item.get("lat"));
                simplified.put("lon", item.get("lon"));

                results.add(simplified);
            }

            // =========================
            // 3. RESPONSE
            // =========================
            Map<String, Object> response = new HashMap<>();
            response.put("results", results);

            return response;

        } catch (Exception e) {
            return Map.of(
                    "error", "Server error",
                    "details", e.getMessage()
            );
        }
    }
    
    
    
    public Map<String, Object> getElevation(String lat, String lon) {

        String url = "https://api.opentopodata.org/v1/srtm90m?locations="
                + lat + "," + lon;

        ResponseEntity<Map> response =
                restTemplate.getForEntity(url, Map.class);

        Map body = response.getBody();
        List results = (List) body.get("results");
        Map result = (Map) results.get(0);

        double meters = ((Number) result.get("elevation")).doubleValue();
        double feet = meters * 3.28084;

        return Map.of(
                "meters", meters,
                "feet", feet,
                "lat", lat,
                "lon", lon
        );
    }
    
    
    
}