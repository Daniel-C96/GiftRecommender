package com.example.GiftRecommender.controller;

import com.example.GiftRecommender.model.Gift;
import com.example.GiftRecommender.service.GeminiService;
import com.example.GiftRecommender.dto.GeminiRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @Autowired
    GeminiService geminiService;

    @CrossOrigin(origins = {"http://localhost:4200", "https://gift-recommender-front.vercel.app/"})
    @PostMapping("/request")
    private List<Gift> giftRequest(@Valid @RequestBody GeminiRequest request) {
        return geminiService.giftRequest(request);
    }

    @GetMapping("/test")
    private String test() {
        return "Test endpoint";
    }
}
