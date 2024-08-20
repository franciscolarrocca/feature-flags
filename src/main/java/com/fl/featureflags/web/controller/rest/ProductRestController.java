package com.fl.featureflags.web.controller.rest;

import com.fl.featureflags.config.DiscountFeatureFlags;
import com.fl.featureflags.domain.Product;
import com.fl.featureflags.respository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.togglz.core.manager.FeatureManager;

import java.util.List;
import java.util.function.Consumer;

import static com.fl.featureflags.domain.ProductType.ELECTRONIC;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private final ProductRepository productRepository;
    private final FeatureManager featureManager;

    public ProductRestController(ProductRepository productRepository, FeatureManager featureManager) {
        this.productRepository = productRepository;
        this.featureManager = featureManager;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        if (featureManager.isActive(DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT)) {
            int discountPercentage = DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT.getPercentage();
            String discountDescription =  DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT.getDescription();

            return productRepository.findAll()
                    .stream()
                    .peek(applyDiscount(discountPercentage, discountDescription))
                    .toList();
        }
        return productRepository.findAll();
    }

    @GetMapping("/products/{type}")
    public List<Product> getProductsByType(@PathVariable String type) {
        if (isElectronicTypeAndExistDiscount(type)) {
            int discountPercentage = DiscountFeatureFlags.ELECTRONIC_DISCOUNT.getPercentage();
            String discountDescription =  DiscountFeatureFlags.ELECTRONIC_DISCOUNT.getDescription();

            return productRepository
                    .findAllProductsByType(type)
                    .stream()
                    .peek(applyDiscount(discountPercentage, discountDescription))
                    .toList();
        }
        return productRepository.findAllProductsByType(type);
    }

    private boolean isElectronicTypeAndExistDiscount(String type) {
        return type.equals(ELECTRONIC.name()) && featureManager.isActive(DiscountFeatureFlags.ELECTRONIC_DISCOUNT);
    }

    private Consumer<Product> applyDiscount(int percentage, String description) {
        return product -> {
            double productPrice = product.getPrice();
            double priceWithDiscount = productPrice - (productPrice * percentage / 100);
            product.setDiscountValues(description, priceWithDiscount);
        };
    }



}