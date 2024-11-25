// package dev.sambhav.mcf.service;

// import dev.sambhav.mcf.client.AmazonSpApiClient;
// import dev.sambhav.mcf.model.Order;
// import dev.sambhav.mcf.repository.OrderRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.amazon.spapi.client.ApiException;
// import com.amazon.spapi.model.orders.OrderList;
// import com.amazon.spapi.model.orders.Order as AmazonOrder;

// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// public class OrderServiceImpl implements OrderService {

//     private final OrderRepository orderRepository;
//     private final AmazonSpApiClient amazonSpApiClient;

//     @Autowired
//     public OrderServiceImpl(OrderRepository orderRepository, AmazonSpApiClient amazonSpApiClient) {
//         this.orderRepository = orderRepository;
//         this.amazonSpApiClient = amazonSpApiClient;
//     }

//     @Override
//     public void updateOrderStatus() {
//         try {
//             List<AmazonOrder> amazonOrders = amazonSpApiClient.getOrderUpdates();

//             for (AmazonOrder amazonOrder : amazonOrders) {
//                 // Find the order in the database
//                 Order order = orderRepository.findByAmazonMcfOrderId(amazonOrder.getAmazonOrderId());

//                 if (order != null) {
//                     // Update the order status
//                     String mappedStatus = mapAmazonOrderStatus(amazonOrder.getOrderStatus().getValue());
//                     order.setStatus(mappedStatus);

//                     // Update delivery ETA if available
//                     if (amazonOrder.getEarliestDeliveryDate() != null) {
//                         order.setDeliveryEta(amazonOrder.getEarliestDeliveryDate().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
//                     }

//                     orderRepository.save(order);
//                 } else {
//                     // Optionally handle orders not found in the database
//                 }
//             }
//         } catch (ApiException e) {
//             e.printStackTrace();
//             // Handle exception appropriately
//         }
//     }

//     private String mapAmazonOrderStatus(String amazonOrderStatus) {
//         switch (amazonOrderStatus) {
//             case "PendingAvailability":
//             case "Pending":
//                 return "Pending";
//             case "Unshipped":
//             case "PartiallyShipped":
//                 return "In Progress";
//             case "Shipped":
//                 return "Shipped";
//             case "Canceled":
//                 return "Cancelled";
//             case "Delivered":
//                 return "Delivered";
//             default:
//                 return "Pending"; // Default to Pending
//         }
//     }
// }
