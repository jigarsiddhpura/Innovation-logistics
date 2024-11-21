// File: src/main/java/dev/sambhav/mcf/service/SellerServiceImpl.java
package dev.sambhav.mcf.service;

import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Seller addSeller(Seller seller) {
        // Hash the password before saving
        seller.setPasswordHash(passwordEncoder.encode(seller.getPasswordHash()));
        return sellerRepository.save(seller);
    }

    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + id));
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller updateSeller(Long id, Seller seller) {
        Optional<Seller> existingSeller = sellerRepository.findById(id);
        if (existingSeller.isPresent()) {
            Seller s = existingSeller.get();
            s.setName(seller.getName());
            s.setEmail(seller.getEmail());
            s.setPhone(seller.getPhone());

            // Update password if provided
            if (seller.getPasswordHash() != null && !seller.getPasswordHash().isEmpty()) {
                s.setPasswordHash(passwordEncoder.encode(seller.getPasswordHash()));
            }

            s.setShopifyStoreUrl(seller.getShopifyStoreUrl());
            s.setAmazonMcfAccountId(seller.getAmazonMcfAccountId());
            return sellerRepository.save(s);
        } else {
            return null; // Or throw an exception
        }
    }

    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }

    public Optional<Seller> findById(Long id) {
        return sellerRepository.findById(id);
    }
}
