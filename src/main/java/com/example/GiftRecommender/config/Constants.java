package com.example.GiftRecommender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    @Value("${API_TOKEN:default}")
    private String apiToken;
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public String getAPI_URL() {
        return API_URL + apiToken;
    }

    @Value("${CUSTOM_SEARCH_API:default}")
    private String customSearchKey;

    @Value("${CUSTOM_SEARCH_CX:default}")
    private String customSearchCx;

    public String getCUSTOM_SEARCH_URL() {
        return "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&searchType=image&q=".formatted(customSearchKey, customSearchCx);
    }
}
