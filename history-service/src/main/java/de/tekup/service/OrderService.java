package de.tekup.service;

import de.tekup.entity.Order;
import de.tekup.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }
    
    public Order findOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).orElseThrow(
                () -> new IllegalArgumentException("order with order number " + orderNumber + " not found")
        );
    }
    
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
