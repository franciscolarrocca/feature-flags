package com.fl.featureflags.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String name, String description, double price, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }

    public void setDiscountValues(String discountDescription, double newPrice) {
        this.setDescription(getDescription() + discountDescription);
        this.setPrice(newPrice);
    }
}