package com.example.GiftRecommender.controller;

import com.example.GiftRecommender.config.Constants;
import com.example.GiftRecommender.model.Gift;
import com.example.GiftRecommender.service.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import dto.GeminiRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @Autowired
    Constants constants;

    @Autowired
    GeminiService geminiService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/request")
    private List<Gift> giftRequest(@Valid @RequestBody GeminiRequest request) {
        return geminiService.giftRequest(request);
    }

    @GetMapping("/test")
    private String test() {
        System.out.println(constants.getAPI_URL());
        return "Test endpoint for bucket4j";
    }
}
