package com.fl.featureflags.config;

import jdk.jfr.Label;
import org.togglz.core.Feature;

public enum DiscountFeatureFlags implements Feature {

    @Label("Price discount for electronics products")
    ELECTRONIC_DISCOUNT(30," - ¡ELECTRONIC DISCOUNT!"),

    @Label("Price discount for all products only available at Nov. 2023")
    NOVEMBER_2023_DISCOUNT(10," - ¡DISCOUNT!");

    private final int percentage;
    private final String description;

    private DiscountFeatureFlags(int percentage, String description) {
        this.percentage = percentage;
        this.description = description;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getDescription() {
        return description;
    }
}