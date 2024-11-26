package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByAmazonMcfOrderId(String amazonMcfOrderId);
    Order findBySellerId(Long sellerId);
    Optional<Order> findById(Long id);

    @Query("SELECT o.fulfillmentStatus FROM Order o WHERE o.orderId = :orderId")
    OrderStatus getOrderStatus(@Param("orderId") Long orderId);

    List<Order> findByStoreUrlAndFulfillmentStatusNot(String storeUrl, OrderStatus status);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByStoreUrl(String storeUrl);
    void deleteByStoreUrl(String storeUrl);
    List<Order> findByStoreUrlAndFulfillmentStatus(String storeUrl, OrderStatus status);


}
