package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.OrderMapper;
import dev.sambhav.mcf.Mapper.ProductMapper;
import dev.sambhav.mcf.dto.*;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
        if (order.getFulfillmentStatus() == null) {
            order.setFulfillmentStatus(OrderStatus.PENDING);
        }

        return orderRepository.save(order); // Saving the order
    }

    public List<Order> getAllOrders(String storeUrl) {
        List<Order> orders;
        if (storeUrl != null && !storeUrl.isEmpty()) {
            String baseUrl = extractBaseUrl(storeUrl)+'/';
            log.info(baseUrl);
            return orderRepository.findByStoreUrl(baseUrl);
        }
        return orderRepository.findAll();
    }

    public OrderResponseDTO getProductById(Long id) {
        Order ordered = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
        log.info(ordered.toString());
        return OrderMapper.toResponseDTO(ordered);
    }
    public String extractBaseUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();

            // Remove trailing slash if present
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }

            // Case 1: myshopify.com domain - keep everything before /products, /orders etc.
            if (host != null && host.contains("myshopify.com")) {
                return uri.getScheme() + "://" + host;
            }

            // Case 2: mydukaan.io pattern - keep up to store identifier
            if (host != null && host.contains("mydukaan.io")) {
                String[] pathSegments = path.substring(1).split("/");
                if (pathSegments.length > 0) {
                    return uri.getScheme() + "://" + host + "/" + pathSegments[0];
                }
            }

            return url;
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public List<Order> getOrdersByStoreUrl(String storeUrl) {
        validateStoreUrl(storeUrl);
        return orderRepository.findByStoreUrl(storeUrl);
    }

    private void validateStoreUrl(String storeUrl) {
        if (storeUrl == null || storeUrl.isEmpty()) {
            throw new IllegalArgumentException("Store URL cannot be empty");
        }

        // Validate store URL format and determine store type
        StoreType storeType = null;
        if (storeUrl.contains("shopify.com")) {
            storeType = StoreType.SHOPIFY;
        } else if (storeUrl.contains("dukaan.com")) {
            storeType = StoreType.DUKAAN;
        } else {
            throw new IllegalArgumentException("Invalid store URL format. Must be from Shopify or Dukaan.");
        }
    }

    public TrackingStatusDTO track(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
            
        TrackingStatusDTO tracking = new TrackingStatusDTO();
        tracking.setCurrentStatus(formatStatus(order.getFulfillmentStatus()));
        
        // Calculate milestone dates
        LocalDateTime shippedDate = order.getCreatedAt();
        LocalDateTime outForDeliveryDate = shippedDate.plusDays(1);
        LocalDateTime estimatedDeliveryDate = shippedDate.plusDays(3);
        
        OrderStatus currentStatus = order.getFulfillmentStatus();
        
        List<TrackingStatusDTO.TrackingMilestone> milestones = new ArrayList<>();
        
        // Shipped milestone
        TrackingStatusDTO.TrackingMilestone shipped = new TrackingStatusDTO.TrackingMilestone();
        shipped.setStatus("Shipped");
        shipped.setDate(shippedDate);
        shipped.setCompleted(isStatusCompleted(shippedDate));
        shipped.setDisplayText("Shipped");
        milestones.add(shipped);
        
        // Out for delivery milestone
        TrackingStatusDTO.TrackingMilestone outForDelivery = new TrackingStatusDTO.TrackingMilestone();
        outForDelivery.setStatus("OutForDelivery");
        outForDelivery.setDate(outForDeliveryDate);
        outForDelivery.setCompleted(isStatusCompleted(outForDeliveryDate));
        outForDelivery.setDisplayText("Out For Delivery");
        milestones.add(outForDelivery);
        
        // MCF (Delivery) milestone
        TrackingStatusDTO.TrackingMilestone mcf = new TrackingStatusDTO.TrackingMilestone();
        mcf.setStatus("Delivered");
        mcf.setDate(estimatedDeliveryDate);
        mcf.setCompleted(isStatusCompleted(estimatedDeliveryDate));
        mcf.setDisplayText("Expected by, " + formatDate(estimatedDeliveryDate));
        milestones.add(mcf);
        
        tracking.setMilestones(milestones.toArray(new TrackingStatusDTO.TrackingMilestone[0]));
        return tracking;
    }

    public static boolean isStatusCompleted(LocalDateTime date) {
        return date.isBefore(LocalDateTime.now()) || date.isEqual(LocalDateTime.now());
    }
    
    public static String formatStatus(OrderStatus status) {
        return switch (status) {
            case SHIPPED -> "Your order has been shipped";
            case IN_PROGRESS -> "Your order is out for delivery";
            case DELIVERED -> "Your order has been delivered";
            case PENDING -> "Your order is being processed";
            default -> "Order status unknown";
        };
    }

    public static String formatDate(LocalDateTime date) {
        // Format: "Mon 15th"
        return date.format(DateTimeFormatter.ofPattern("EEE dd'th'"));
    }

    @Transactional
    public Order fulfillOrder(Long orderId) {
        // Fetching and validating the order
        Order order = getOrderById(orderId);

        // Ensuring the order is in the correct state
        if (order.getFulfillmentStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is already fulfilled or in progress.");
        }

        // Updating the status
        order.setFulfillmentStatus(OrderStatus.IN_PROGRESS);

        return orderRepository.save(order); // Saving the updated order
    }

    // Helper method to fetch an order by ID
    private Order getOrderById(Long orderId) {
        // TODO: FIND BY ORDERID wasn't working
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    // Helper method to fetch a seller by ID
    private SellerResponseDTO getSellerById(Long sellerId) {
        return sellerService.getSellerById(sellerId);
    }

//    @Transactional
//    public void saveOrderFromWebhook(String payload) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = objectMapper.readTree(payload);
//
//        // Extract order details
//        Order order = new Order();
//        order.setOrderId(rootNode.get("order_number").asLong());
//        order.setSellerId(rootNode.has("seller_id") ? rootNode.get("seller_id").asLong() : 1); // Handle optional field
//        order.setCustomerName(rootNode.get("customer").get("first_name").asText() + " " + rootNode.get("customer").get("last_name").asText());
//        order.setEmail(rootNode.has("contact_email") ? rootNode.get("contact_email").asText() : null);
//        order.setCurrentTotalPrice(new BigDecimal(rootNode.get("current_total_price").asText()));
//        order.setFulfillmentStatus(OrderStatus.PENDING);
//
//        // Handle boolean field
//        order.setSlaMet(rootNode.has("sla_met") && !rootNode.get("sla_met").isNull() ? rootNode.get("sla_met").asBoolean() : null);
//
//        // Parse timestamps
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//        if (rootNode.has("created_at") && !rootNode.get("created_at").isNull()) {
//            order.setCreatedAt(LocalDateTime.parse(rootNode.get("created_at").asText(), formatter));
//        }
//        if (rootNode.has("processed_at") && !rootNode.get("processed_at").isNull()) {
//            order.setProcessedAt(OffsetDateTime.parse(rootNode.get("processed_at").asText(), formatter).toLocalDateTime());
//        }
//        if (rootNode.has("delivery_eta") && !rootNode.get("delivery_eta").isNull()) {
//            order.setDeliveryEta(OffsetDateTime.parse(rootNode.get("delivery_eta").asText(), formatter).toLocalDateTime());
//        }
//
//        // Save the order to the database
//        orderRepository.save(order);
//    }


    @Transactional
    public void saveOrder(OrderDTO orderDto) {
        log.info(orderDto.getStoreUrl());
        Order order = mapToEntity(orderDto);
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrder(OrderDTO orderDto) {
        Order order = orderRepository.findById(orderDto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderDto.getOrderId()));
        order.setOrderName(order.getOrderName());
        order.setCustomerName(orderDto.getCustomerName());
        order.setEmail(orderDto.getEmail());
        order.setCurrentTotalPrice(orderDto.getCurrentTotalPrice());
        order.setFulfillmentStatus(orderDto.getFulfillmentStatus());
        order.setProcessedAt(orderDto.getProcessedAt());

        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    private Order mapToEntity(OrderDTO orderDto) {
        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setSellerId(orderDto.getSellerId());
        order.setOrderName(orderDto.getOrderName());
        order.setAmazonMcfOrderId(orderDto.getAmazonMcfOrderId());
        order.setCustomerName(orderDto.getCustomerName());
        order.setEmail(orderDto.getEmail());
        order.setCurrentTotalPrice(orderDto.getCurrentTotalPrice());
        order.setFulfillmentStatus(orderDto.getFulfillmentStatus());
        order.setSlaMet(orderDto.getSlaMet());
        order.setDeliveryEta(orderDto.getDeliveryEta());
        order.setCreatedAt(orderDto.getCreatedAt());
        order.setProcessedAt(orderDto.getProcessedAt());
        order.setStoreUrl(orderDto.getStoreUrl());
        order.setStoreType(orderDto.getStoreType());


        return order;
    }

    @Transactional
    public OrderDTO markOrderAsShipped(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Error("Order not found with ID: " + orderId));

        if (order.getFulfillmentStatus() != OrderStatus.PENDING) {
            throw new Error(
                    "Current status: " + order.getFulfillmentStatus());
        }

        order.setFulfillmentStatus(OrderStatus.SHIPPED);
        order.setProcessedAt(LocalDateTime.now());

        // Update delivery ETA - assuming 2 days for delivery after shipping
        order.setDeliveryEta(LocalDateTime.now().plusDays(2));

        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO markOrderDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Error("Order not found with ID: " + orderId));

        if (order.getFulfillmentStatus() != OrderStatus.SHIPPED) {
            throw new Error(
                    "Current status: " + order.getFulfillmentStatus());
        }

        order.setFulfillmentStatus(OrderStatus.DELIVERED);
        order.setProcessedAt(LocalDateTime.now());

        // Update delivery ETA to end of current day
        order.setDeliveryEta(LocalDateTime.now().withHour(20).withMinute(0).withSecond(0));

        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }
    public static OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setSellerId(order.getSellerId());
        dto.setOrderName(order.getOrderName());
        dto.setAmazonMcfOrderId(order.getAmazonMcfOrderId());
        dto.setCustomerName(order.getCustomerName());
        dto.setEmail(order.getEmail());
        dto.setCurrentTotalPrice(order.getCurrentTotalPrice());
        dto.setFulfillmentStatus(order.getFulfillmentStatus());
        dto.setSlaMet(order.getSlaMet());
        dto.setDeliveryEta(order.getDeliveryEta());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setProcessedAt(order.getProcessedAt());
        dto.setStoreUrl(order.getStoreUrl());
        dto.setStoreType(order.getStoreType());
        return dto;
    }

}
    




