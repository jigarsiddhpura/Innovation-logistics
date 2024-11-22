// File: src/main/java/dev/sambhav/mcf/repository/SellerRepository.java
package dev.sambhav.mcf.repository;

import dev.sambhav.mcf.model.Seller;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
    Seller findByPhone(String phone);
    Optional<Seller> findById(Long id);
}
