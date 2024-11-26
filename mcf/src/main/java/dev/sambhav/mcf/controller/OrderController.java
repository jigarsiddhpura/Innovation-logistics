package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.Mapper.OrderMapper;
import dev.sambhav.mcf.dto.*;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.service.ApiResponse;
import dev.sambhav.mcf.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired(required = false)
    private OrderService orderService;

    @PostMapping("orders/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO dto) {
        // Modularized naming and added ResponseEntity for consistency
        Order createdOrder = orderService.saveOrderToMcf(OrderMapper.toEntity(dto));
        return ResponseEntity.ok(OrderMapper.toResponseDTO(createdOrder));
    }

    @PutMapping("orders/fulfill/{orderId}")
    public ResponseEntity<OrderResponseDTO> fulfillOrder(@PathVariable Long orderId) {
        // Modularized naming and added ResponseEntity for consistency
        Order updatedOrder = orderService.fulfillOrder(orderId);
        return ResponseEntity.ok(OrderMapper.toResponseDTO(updatedOrder));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String storeUrl) {
        List<Order> orders = orderService.getAllOrders(storeUrl);
        List<OrderDTO> ordersDTOS = orders.stream()
                .map(OrderService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordersDTOS);
    }
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDTO> getProductById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getProductById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/track/{orderId}")
    public ResponseEntity<TrackingStatusDTO> trackOrder(@PathVariable Long orderId) {
        // OrderStatus status = orderService.track(orderId);
        // OrderStatusDTO statusDTO = OrderMapper.toOrderStatusDTO(status);
        // return ResponseEntity.ok(statusDTO);
        TrackingStatusDTO trackingInfo = orderService.track(orderId);
        return ResponseEntity.ok(trackingInfo);
    }

    @PutMapping("/{orderId}/mark-shipped")
    public ResponseEntity<?> markOrderAsShipped(@PathVariable Long orderId) {
        try {
            OrderDTO updatedOrder = orderService.markOrderAsShipped(orderId);
            return ResponseEntity.ok(new ApiResponse("Order marked as shipped", true, updatedOrder));
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false, null));
        }
    }

    @PutMapping("/{orderId}/mark-delivered")
    public ResponseEntity<?> markOrderAsOutForDelivery(@PathVariable Long orderId) {
        try {
            OrderDTO updatedOrder = orderService.markOrderDelivered(orderId);
            return ResponseEntity.ok(new ApiResponse("Order marked as out for delivery", true, updatedOrder));
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false, null));
        }
    }
}
