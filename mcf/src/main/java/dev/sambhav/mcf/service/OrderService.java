package dev.sambhav.mcf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.OrderRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SellerService sellerRepository;

    @Transactional
    public Order saveOrderToMcf(Order order) {
        // Check if seller exists using SellerRepository
        Optional<Seller> seller = sellerRepository.findById(order.getSellerId());
        
        // Throw exception if seller not found
        if (seller.isEmpty()) {
            throw new IllegalArgumentException("Seller not found with id: " + order.getSellerId());
        }
        
        // Save order if seller exists
        // System.out.println(savedOrder);
        return orderRepository.save(order);
        // return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderStatus track(Long orderId) {
        return orderRepository.getOrderStatus(orderId);
    }
}
