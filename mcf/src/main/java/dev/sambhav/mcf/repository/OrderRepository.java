// File: src/main/java/dev/sambhav/mcf/repository/OrderRepository.java
package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Order;

import org.springframework.data.repository.ListCrudRepository;
// import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ListCrudRepository<Order, Long> {
    Order findByAmazonMcfOrderId(String amazonMcfOrderId);
}
