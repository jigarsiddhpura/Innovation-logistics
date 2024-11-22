package dev.sambhav.mcf.Mapper;

import dev.sambhav.mcf.dto.AllOrdersDTO;
import dev.sambhav.mcf.dto.OrderRequestDTO;
import dev.sambhav.mcf.dto.OrderResponseDTO;
import dev.sambhav.mcf.dto.OrderStatusDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import lombok.Data;

@Data
public class OrderMapper {

    public static Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setSellerId(dto.getSellerId());
        order.setShopifyOrderId(dto.getShopifyOrderId());
        order.setAmazonMcfOrderId(dto.getAmazonMcfOrderId());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());
        order.setTotalPrice(dto.getTotalPrice());
        order.setSlaMet(dto.getSlaMet());
        order.setDeliveryEta(dto.getDeliveryEta());
        return order;
    }

    public static OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getOrderId(),
                order.getShopifyOrderId(),
                order.getAmazonMcfOrderId(),
                order.getCustomerName(),
                order.getStatus().name(),
                order.getUpdatedAt()
        );
    }

    public static AllOrdersDTO getAllOrders(Order order) {
        return new AllOrdersDTO(
                order.getOrderId(),
                order.getShopifyOrderId(),
                order.getStatus().name(),
                order.getTotalPrice()
        );
    }

    public static OrderStatusDTO toOrderStatusDTO(OrderStatus status) {
        String message = generateStatusMessage(status); // Optional message generator
        return new OrderStatusDTO(status.name(), message);
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

