package com.example.GiftRecommender.service;

import com.example.GiftRecommender.config.Constants;
import com.example.GiftRecommender.model.Gift;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.GiftRecommender.dto.GeminiRequest;
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
            "  'occasion':'birthday'\n" +
            "  'who':'family'\n" +
            "  'hobbies': ['video games, sports'],\n" +
            "  'minBudget': 10,\n" +
            "  'maxBudget': 150\n" +
            "  'description': 'he always played video games'\n" +
            "  'language': 'en'\n" +
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
            "Ensure the suggestions are personalized. All fields are optional, so if you receive a JSON with" +
            "no parameters generate gifts that might be popular with everybody." +
            "Use the language that you get provided in the 'language' field";

    public List<Gift> giftRequest(@Valid GeminiRequest geminiRequest) {
        String inputText = createInputText(geminiRequest);
        String requestBody = createRequestBody(inputText);

        try {
            String jsonText = sendRequest(requestBody);
            List<Gift> gifts = parseGiftResponse(jsonText, geminiRequest);
            System.out.println(geminiRequest);
            return gifts;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null in case of error
    }

    private String createInputText(GeminiRequest geminiRequest) {
        return """
                {
                  'age': %d,
                  'gender': '%s',
                  'occasion': '%s',
                  'who': '%s',
                  'hobbies': '%s',
                  'minBudget' : %d,
                  'maxBudget' : %d,
                  'description': '%s',
                  'language': '%s'
                }
                """.formatted(
                geminiRequest.getAge(),
                geminiRequest.getGender(),
                geminiRequest.getOccasion(),
                geminiRequest.getWho(),
                Arrays.toString(geminiRequest.getHobbies()),
                geminiRequest.getMinBudget(),
                geminiRequest.getMaxBudget(),
                geminiRequest.getDescription(),
                geminiRequest.getLanguage()
        );
    }

    private String createRequestBody(String inputText) {
        return """
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
    }

    private String sendRequest(String requestBody) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(constants.getAPI_URL()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());
        return rootNode
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
    }

    private List<Gift> parseGiftResponse(String jsonText, GeminiRequest geminiRequest) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode giftArray = objectMapper.readTree(jsonText);
        List<Gift> gifts = new ArrayList<>();

        for (JsonNode giftNode : giftArray) {
            Gift gift = objectMapper.treeToValue(giftNode, Gift.class);
            gift.setImageUrl(imageSearchService.retrieveImageUrl(gift.getName()));

            if (isGiftWithinBudget(geminiRequest, gift)) {
                gifts.add(gift);
            }
        }

        return gifts;
    }

    private boolean isGiftWithinBudget(GeminiRequest geminiRequest, Gift gift) {
        return (geminiRequest.getMinBudget() == null || gift.getPrice() >= geminiRequest.getMinBudget()) &&
                (geminiRequest.getMaxBudget() == null || gift.getPrice() <= geminiRequest.getMaxBudget());
    }
}
