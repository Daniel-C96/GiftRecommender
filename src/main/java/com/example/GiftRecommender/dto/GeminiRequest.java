package com.example.GiftRecommender.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeminiRequest {

    @Min(value = 0, message = "Age cannot be less than 0.")
    @Max(value = 150, message = "Age cannot be greater than 150.")
    private int age;

    @Size(max = 15)
    private String gender;

    @Size(max = 20)
    private String occasion;

    @Size(max = 20)
    private String who;

    private String[] hobbies;

    @Size(max = 100)
    private String description;

    @Min(value = 0, message = "Minimum budget cannot be less than 0.")
    @Max(value = 100_000_000, message = "Minimum budget cannot be greater than 100,000,000.")
    private Integer minBudget;

    @Min(value = 0, message = "Maximum budget cannot be less than 0.")
    @Max(value = 100_000_000, message = "Maximum budget cannot be greater than 100,000,000.")
    private Integer maxBudget;

    private String language;

    // Ensures that maxBudget is not smaller than minBudget.
    @AssertTrue(message = "Maximum budget cannot be smaller than the minimum budget.")
    public boolean isValidBudget() {
        if (minBudget != null && maxBudget != null) {
            return maxBudget >= minBudget;
        }
        return true;
    }
}
