package com.fl.featureflags.respository;

import com.fl.featureflags.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllProductsByType(String type);
}
