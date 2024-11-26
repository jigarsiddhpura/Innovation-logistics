// File: src/main/java/dev/sambhav/mcf/repository/ProductRepository.java
package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.dto.StoreType;
import dev.sambhav.mcf.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByVendor(String vendor);
    List<Product> findByStoreUrl(String storeUrl);
    List<Product> findByStoreType(StoreType storeType);
    void deleteByStoreUrl(String storeUrl);

}
