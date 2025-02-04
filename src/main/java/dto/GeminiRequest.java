package dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeminiRequest {

    @NotNull(message = "Age is null.")
    private int age;

    @NotNull(message = "Gender is null.")
    @NotEmpty(message = "Gender is empty.")
    private String gender;

    @NotNull(message = "Hobbies is null.")
    @NotEmpty(message = "Hobbies is empty.")
    private String[] hobbies;

    private String description;

    private int minBudget;

    private int maxBudget;

}
