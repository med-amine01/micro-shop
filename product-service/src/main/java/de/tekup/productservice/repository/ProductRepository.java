package de.tekup.productservice.repository;

import de.tekup.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    boolean existsBySkuCode(String skuCode);
    Optional<Product> findBySkuCode(String skuCode);
    List<Product> findByCouponCode(String couponCode);
}
