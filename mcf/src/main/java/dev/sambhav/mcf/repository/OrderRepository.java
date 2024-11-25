package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByAmazonMcfOrderId(String amazonMcfOrderId);
    Order findBySellerId(Long sellerId);

    @Query("SELECT o.fulfillmentStatus FROM Order o WHERE o.orderId = :orderId")
    OrderStatus getOrderStatus(@Param("orderId") Long orderId);

    Page<Order> findByFulfillmentStatusNot(OrderStatus status, Pageable pageable);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


}
