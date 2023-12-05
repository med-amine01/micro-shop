package de.tekup.repository;

import de.tekup.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
