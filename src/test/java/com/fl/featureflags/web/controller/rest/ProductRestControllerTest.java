package com.fl.featureflags.web.controller.rest;

import com.fl.featureflags.config.DiscountFeatureFlags;
import com.fl.featureflags.domain.Product;
import com.fl.featureflags.respository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.togglz.core.manager.FeatureManager;

import java.util.List;

import static com.fl.featureflags.domain.ProductType.ELECTRONIC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
public class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private FeatureManager featureManager;

    @Test
    void getAllProducts_withoutDiscounts_200OK() throws Exception {
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", 10.0, "ELECTRONIC"),
                new Product("Product 2", "Description 2", 15.0, "OTHER"),
                new Product("Product 3", "Description 3", 20.0, "ELECTRONIC")
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(featureManager.isActive(DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT)).thenReturn(false);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].price", Matchers.equalTo(10.0)))
                .andExpect(jsonPath("$[1].price", Matchers.equalTo(15.0)))
                .andExpect(jsonPath("$[2].price", Matchers.equalTo(20.0)));
    }

    @Test
    void getAllProducts_withNovember2023Discount_200OKAndDiscountApplied() throws Exception {
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", 10.0, "ELECTRONIC"),
                new Product("Product 2", "Description 2", 15.0, "OTHER"),
                new Product("Product 3", "Description 3", 20.0, "ELECTRONIC")
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(featureManager.isActive(DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT)).thenReturn(true);

        String november2023DiscountDescription = DiscountFeatureFlags.NOVEMBER_2023_DISCOUNT.getDescription();

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].price", Matchers.equalTo(9.0)))
                .andExpect(jsonPath("$[0].description", Matchers.containsString(november2023DiscountDescription)))
                .andExpect(jsonPath("$[1].price", Matchers.equalTo(13.5)))
                .andExpect(jsonPath("$[1].description", Matchers.containsString(november2023DiscountDescription)))
                .andExpect(jsonPath("$[2].price", Matchers.equalTo(18.0)))
                .andExpect(jsonPath("$[2].description", Matchers.containsString(november2023DiscountDescription)));
    }

    @Test
    void getProductsByType_withoutElectronicDiscount_200OK() throws Exception {
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", 10.0, "ELECTRONIC"),
                new Product("Product 3", "Description 3", 20.0, "ELECTRONIC")
        );
        String electronicType = ELECTRONIC.name();
        Mockito.when(productRepository.findAllProductsByType(electronicType)).thenReturn(products);
        Mockito.when(featureManager.isActive(DiscountFeatureFlags.ELECTRONIC_DISCOUNT)).thenReturn(false);

        mockMvc.perform(get("/api/products/"+ electronicType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].price", Matchers.equalTo(10.0)))
                .andExpect(jsonPath("$[1].price", Matchers.equalTo(20.0)));
    }

    @Test
    void getProductsByType_withElectronicDiscount_200OKAndDiscountApplied() throws Exception {
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", 10.0, "ELECTRONIC"),
                new Product("Product 3", "Description 3", 20.0, "ELECTRONIC")
        );
        String electronicType = ELECTRONIC.name();
        String electronicDiscountDescription = DiscountFeatureFlags.ELECTRONIC_DISCOUNT.getDescription();

        Mockito.when(productRepository.findAllProductsByType(electronicType)).thenReturn(products);
        Mockito.when(featureManager.isActive(DiscountFeatureFlags.ELECTRONIC_DISCOUNT)).thenReturn(true);

        mockMvc.perform(get("/api/products/"+ electronicType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].price", Matchers.equalTo(7.0)))
                .andExpect(jsonPath("$[0].description", Matchers.containsString(electronicDiscountDescription)))
                .andExpect(jsonPath("$[1].price", Matchers.equalTo(14.0)))
                .andExpect(jsonPath("$[0].description", Matchers.containsString(electronicDiscountDescription)));
    }

}