package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.Mapper.OrderMapper;
import dev.sambhav.mcf.dto.AllOrdersDTO;
import dev.sambhav.mcf.dto.OrderResponseDTO;
import dev.sambhav.mcf.dto.OrderStatusDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired(required = false)
    private OrderService orderService;

    @PostMapping("orders/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody Order order) {
        // Modularized naming and added ResponseEntity for consistency
        Order createdOrder = orderService.saveOrderToMcf(order);
        return ResponseEntity.ok(OrderMapper.toResponseDTO(order));
    }

    @PutMapping("orders/{orderId}/fulfill")
    public ResponseEntity<OrderResponseDTO> fulfillOrder(@PathVariable Long orderId) {
        // Modularized naming and added ResponseEntity for consistency
        Order updatedOrder = orderService.fulfillOrder(orderId);
        return ResponseEntity.ok(OrderMapper.toResponseDTO(updatedOrder));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<AllOrdersDTO>> getAllOrders() {
        // Added ResponseEntity for standardizing responses
        List<Order> orders = orderService.getAllOrders();
        List<AllOrdersDTO> ordersDTOS=orders.stream().map(OrderMapper::getAllOrders).toList();
        return ResponseEntity.ok(ordersDTOS);
    }

    @GetMapping("/orders/{orderId}/track")
    public ResponseEntity<OrderStatusDTO> trackOrder(@PathVariable Long orderId) {
        OrderStatus status = orderService.track(orderId);
        OrderStatusDTO statusDTO = OrderMapper.toOrderStatusDTO(status);
        return ResponseEntity.ok(statusDTO);
    }
}
