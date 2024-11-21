// File: src/main/java/dev/sambhav/mcf/repository/ProductRepository.java
package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Product;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, Long> {

    List<Product> findByName(String name);

}
