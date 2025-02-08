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
    private Constants constants;

    public String retrieveImageUrl(String item) {
        // Build the API URL by encoding the item name
        String apiUrl = constants.getCUSTOM_SEARCH_URL() + URLEncoder.encode(item, StandardCharsets.UTF_8);

        try {
            HttpResponse<String> response = sendHttpRequest(apiUrl);

            // Check if the response status is "Too Many Requests"
            if (response.statusCode() == 429) {
                return "no-more-requests.png";
            }

            return extractImageUrlFromResponse(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    private HttpResponse<String> sendHttpRequest(String apiUrl) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String extractImageUrlFromResponse(String responseBody) throws Exception {
        // Parse the JSON response and extract the image link
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        JsonNode firstItem = rootNode.path("items").get(0);
        if (firstItem != null) {
            return firstItem.path("link").asText(); // Extract the image URL
        }

        return null;
    }
}
