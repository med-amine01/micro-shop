package de.tekup.orderservice.repository;

import de.tekup.orderservice.entity.Order;
import de.tekup.orderservice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String uuid);
    List<Order> findAllByOrderStatusOrderByIdDesc(OrderStatus status);
    List<Order> findAllByOrderByIdDesc();
}
