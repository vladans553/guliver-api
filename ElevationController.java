package com.guliver.api.contoller;

import org.springframework.web.bind.annotation.*;
import com.guliver.api.service.ElevationService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ElevationController {

    private final ElevationService service;

    // API KEY iz Render ENV var
    private static final String API_KEY =
            System.getenv("API_KEY");

    public ElevationController(ElevationService service) {
        this.service = service;
    }

    // =========================
    // VALIDATION METHOD
    // =========================
    private boolean isValidKey(String key) {
        return key != null && API_KEY != null && key.equals(API_KEY);
    }

    // =========================
    // 1. SEARCH (LISTA MESTA)
    // =========================
    @GetMapping("/search")
    public Map<String, Object> searchPlace(
            @RequestParam String place,
            @RequestHeader(value = "x-api-key", required = false) String apiKey
    ) {

        if (!isValidKey(apiKey)) {
            return Map.of("error", "Unauthorized");
        }

        return service.getPlaces(place);
    }

    // =========================
    // 2. ELEVATION
    // =========================
    @GetMapping("/elevation")
    public Map<String, Object> getElevation(
            @RequestParam String lat,
            @RequestParam String lon,
            @RequestHeader(value = "x-api-key", required = false) String apiKey
    ) {

        if (!isValidKey(apiKey)) {
            return Map.of("error", "Unauthorized");
        }

        return service.getElevation(lat, lon);
    }
}