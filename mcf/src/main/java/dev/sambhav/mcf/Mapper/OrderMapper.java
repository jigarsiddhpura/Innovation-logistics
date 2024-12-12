package dev.sambhav.mcf.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dev.sambhav.mcf.dto.*;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import lombok.Data;

@Data
public class OrderMapper {

    // WAS PUBLIC STATIC INITITALLY
    public static Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setSellerId(dto.getSellerId());
        order.setCustomerName(dto.getCustomerName());
        order.setEmail(dto.getEmail());
        order.setCurrentTotalPrice(dto.getCurrentTotalPrice());
        order.setFulfillmentStatus(null);
        order.setSlaMet(dto.getSlaMet());
        order.setStoreUrl(dto.getStoreUrl());
        order.setStoreType(null);

        // Handle LocalDateTime parsing
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        if (dto.getDeliveryEta() != null) {
            order.setDeliveryEta(LocalDateTime.parse(dto.getDeliveryEta(), formatter));
        }
        order.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt(), formatter));
        order.setProcessedAt(LocalDateTime.parse(dto.getProcessedAt(), formatter));

        return order;
    }

    // WAS PUBLIC STATIC INITITALLY
    public static OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO(null, null, null, null, null, null, null, null, null,null,null);
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setCustomerName(order.getCustomerName());
        responseDTO.setEmail(order.getEmail());
        responseDTO.setCurrentTotalPrice(order.getCurrentTotalPrice());
        responseDTO.setFulfillmentStatus(String.valueOf(order.getFulfillmentStatus()));
        responseDTO.setSlaMet(order.getSlaMet());
        responseDTO.setDeliveryEta(order.getDeliveryEta());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setProcessedAt(order.getProcessedAt());
        responseDTO.setStoreUrl(order.getStoreUrl());
        responseDTO.setStoreType(order.getStoreType());

        return responseDTO;
    }

//    public static OrderDTO getAllOrders(Order order) {
//        return new AllOrdersDTO(
//                order.getOrderId(),
//                order.getSellerId(),
//                order.getOrderName(),
//                order.getAmazonMcfOrderId(),
//                order.getCustomerName(),
//                order.getEmail(),
//                order.getCurrentTotalPrice(),
//                order.getFulfillmentStatus(),
//                order.getSlaMet(),
//                order.getDeliveryEta(),
//                order.getCreatedAt(),
//                order.getProcessedAt(),
//                order.getCurrentTotalPrice()
//
//        );
//    }

    public static OrderStatusDTO toOrderStatusDTO(OrderStatus status) {
        String message = generateStatusMessage(status); // Optional message generator
        LocalDateTime delivery_eta = LocalDateTime.now().plusDays(1L);
        Boolean slaMet = (status == OrderStatus.DELIVERED) ? true : false;
        return new OrderStatusDTO(status.name(), delivery_eta, slaMet, message);
    }

    private static String generateStatusMessage(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Your order is pending and awaiting processing.";
            case IN_PROGRESS:
                return "Your order is being processed.";
            case SHIPPED:
                return "Your order has been shipped.";
            case DELIVERED:
                return "Your order has been delivered.";
            case CANCELLED:
                return "Your order was cancelled.";
            default:
                return "Unknown order status.";
        }
    }
}
