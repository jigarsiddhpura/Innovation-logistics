// File: src/main/java/dev/sambhav/mcf/controller/SellerController.java
package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.dto.SellerRequestDTO;
import dev.sambhav.mcf.dto.SellerResponseDTO;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping
    public ResponseEntity<SellerResponseDTO> addSeller(@RequestBody SellerRequestDTO dto) {
        SellerResponseDTO createdSeller = sellerService.addSeller(dto);
        return ResponseEntity.ok(createdSeller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> getSellerById(@PathVariable Long id) {
        SellerResponseDTO seller = sellerService.getSellerById(id);

        return ResponseEntity.ok(seller);
    }

    @GetMapping
    public ResponseEntity<List<SellerResponseDTO>> getAllSellers() {
        List<SellerResponseDTO> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> updateSeller(@PathVariable Long id, @RequestBody SellerRequestDTO dto) {
        SellerResponseDTO updatedSeller = sellerService.updateSeller(id, dto);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
