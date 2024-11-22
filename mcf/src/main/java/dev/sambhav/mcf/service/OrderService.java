package dev.sambhav.mcf.service;

import dev.sambhav.mcf.dto.SellerResponseDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SellerService sellerService; // Renamed for clarity

    @Transactional
    public Order saveOrderToMcf(Order order) {
        // Validating seller existence
        SellerResponseDTO seller = getSellerById(order.getSellerId());

        // Ensuring default status
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        return orderRepository.save(order); // Saving the order
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Fetch all orders
    }

    public OrderStatus track(Long orderId) {
        return orderRepository.getOrderStatus(orderId); // Delegate repository logic
    }

    @Transactional
    public Order fulfillOrder(Long orderId) {
        // Fetching and validating the order
        Order order = getOrderById(orderId);

        // Ensuring the order is in the correct state
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is already fulfilled or in progress.");
        }

        // Updating the status
        order.setStatus(OrderStatus.IN_PROGRESS);

        return orderRepository.save(order); // Saving the updated order
    }

    // Helper method to fetch an order by ID
    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    // Helper method to fetch a seller by ID
    private SellerResponseDTO getSellerById(Long sellerId) {
        return sellerService.getSellerById(sellerId);
    }
}
