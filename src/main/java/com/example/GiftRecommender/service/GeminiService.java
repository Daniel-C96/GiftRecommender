package com.example.GiftRecommender.service;

import com.example.GiftRecommender.config.Constants;
import com.example.GiftRecommender.model.Gift;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GeminiRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GeminiService {

    @Autowired
    private Constants constants;

    @Autowired
    private ImageSearchService imageSearchService;

    private final String INSTRUCTIONS = "You are a gift recommender. You will receive a JSON input similar to the following: \n" +
            "{\n" +
            "  'age': 16,\n" +
            "  'gender': 'male',\n" +
            "  'hobbies': ['video games, sports'],\n" +
            "  'description': 'he always played video games'\n" +
            "  'minBudget': 10,\n" +
            "  'maxBudget': 150\n" +
            "}\n" +
            "Based on the input, you need to provide a JSON output with 3 to 10 gift ideas in the following format: \n" +
            "[\n" +
            "  {\n" +
            "    'name': 'PS5 Controller',\n" +
            "    'price': 60,\n" +
            "    'description': 'A high-quality controller compatible with PS5, perfect for gaming enthusiasts.'\n" +
            "  },\n" +
            "  {\n" +
            "    'name': 'Nike Running Shoes',\n" +
            "    'price': 120,\n" +
            "    'description': 'Comfortable and stylish shoes suitable for both sports and daily use.'\n" +
            "  }\n" +
            "]\n" +
            "Ensure the suggestions are personalized.";


    public List<Gift> giftRequest(@Valid GeminiRequest geminiRequest) {

        // Convert fields from GeminiRequest into a JSON-like string for the prompt
        String inputText = """
                {
                  'age': %d,
                  'gender': '%s',
                  'hobbies': '%s',
                  'description': '%s'
                  'minBudget' : %d,
                  'maxBudget' : %d
                }
                """.formatted(
                geminiRequest.getAge(),
                geminiRequest.getGender(),
                Arrays.toString(geminiRequest.getHobbies()),
                geminiRequest.getDescription(),
                geminiRequest.getMinBudget(),
                geminiRequest.getMaxBudget()
        );

        String requestBody = """
                {
                  "system_instruction": {
                    "parts": {
                      "text": "%s"
                    }
                  },
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 0.5,
                    "maxOutputTokens": 800,
                    "topP": 0.5,
                    "topK": 50,
                    "response_mime_type": "application/json"
                  }
                }
                """.formatted(INSTRUCTIONS, inputText);

        try {
            // Create HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(constants.getAPI_URL()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request and get a response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response to get the "text" content
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            //Extract the content of the "text" inside "candidates" of the response
            String jsonText = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            List<Gift> gifts = new ArrayList<>();
            JsonNode giftArray = objectMapper.readTree(jsonText); // Parse the String to JSON

            //Convert the list of JsonNode to a Gift list
            for (JsonNode giftNode : giftArray) {
                Gift gift = objectMapper.treeToValue(giftNode, Gift.class);
                gift.setImageUrl(imageSearchService.retrieveImageUrl(gift.getName()));
                gifts.add(gift);
            }

            return gifts;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null in case of error
    }
}
