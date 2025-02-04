package com.example.GiftRecommender.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gift {
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
}
