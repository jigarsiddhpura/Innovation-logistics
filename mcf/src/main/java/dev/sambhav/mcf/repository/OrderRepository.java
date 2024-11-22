package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByAmazonMcfOrderId(String amazonMcfOrderId);
    Order findBySellerId(Long sellerId);

    @Query("SELECT o.status FROM Order o WHERE o.orderId = :orderId")
    OrderStatus getOrderStatus(@Param("orderId") Long orderId);
}
