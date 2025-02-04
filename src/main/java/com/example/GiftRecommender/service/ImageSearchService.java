package com.example.GiftRecommender.service;

import com.example.GiftRecommender.config.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class ImageSearchService {

    @Autowired
    Constants constants;

    public String retrieveImageUrl(String item) {

        String apiUrl = constants.getCUSTOM_SEARCH_URL() + URLEncoder.encode(item, StandardCharsets.UTF_8);

        try {
            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // Send the request and receive the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            // Extract the URL of the image from the first element in "items"
            JsonNode firstItem = rootNode.path("items").get(0);
            if (firstItem != null) {
                return firstItem.path("link").asText();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null in case of error
    }
}
