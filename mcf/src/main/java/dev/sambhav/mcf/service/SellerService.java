// File: src/main/java/dev/sambhav/mcf/service/SellerServiceImpl.java
package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.SellerMapper;
import dev.sambhav.mcf.dto.SellerRequestDTO;
import dev.sambhav.mcf.dto.SellerResponseDTO;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.SellerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SellerResponseDTO addSeller(SellerRequestDTO dto) {
        Seller seller = new Seller();
        seller = SellerMapper.toEntity(dto, seller);

        // Hash the password before saving
        seller.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        return SellerMapper.toResponseDTO(sellerRepository.save(seller));
    }

    public SellerResponseDTO getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found with id: " + id));
        return SellerMapper.toResponseDTO(seller);
    }

    public List<SellerResponseDTO> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(SellerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public SellerResponseDTO updateSeller(Long id, SellerRequestDTO dto) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found with id: " + id));

        seller = SellerMapper.toEntity(dto, seller);

        // Update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            seller.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return SellerMapper.toResponseDTO(sellerRepository.save(seller));
    }

    public void deleteSeller(Long id) {
        if (!sellerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found with id: " + id);
        }
        sellerRepository.deleteById(id);
    }
}
