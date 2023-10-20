package de.tekup.productservice.repository;

import de.tekup.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Optional<Product> findBySkuCode(String skuCode);
}
