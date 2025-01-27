package com.example.GiftRecommender.config;

import org.springframework.beans.factory.annotation.Value;

public class Constants {

    @Value("${API_TOKEN}")
    private static String apiToken;
    public static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiToken;
}
