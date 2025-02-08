package dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeminiRequest {

    //@NotNull(message = "Age is null.")
    private int age;

    //@NotNull(message = "Gender is null.")
    //@NotEmpty(message = "Gender is empty.")
    @Size(max = 15)
    private String gender;

    @Size(max = 20)
    private String occasion;

    @Size(max = 20)
    private String who;

    //@NotNull(message = "Hobbies is null.")
    //@NotEmpty(message = "Hobbies is empty.")
    private String[] hobbies;

    @Size(max = 100)
    private String description;

    private Integer minBudget;

    private Integer maxBudget;

    private String language;

}
