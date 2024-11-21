// File: src/main/java/dev/sambhav/mcf/controller/SellerController.java
package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping
    public Seller addSeller(@RequestBody Seller seller) {
        return sellerService.addSeller(seller);
    }

    @GetMapping("/{id}")
    public Seller getSellerById(@PathVariable Long id) {
        return sellerService.getSellerById(id);
    }

    @GetMapping
    public List<Seller> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @PutMapping("/{id}")
    public Seller updateSeller(@PathVariable Long id, @RequestBody Seller seller) {
        return sellerService.updateSeller(id, seller);
    }

    @DeleteMapping("/{id}")
    public void deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
    }
}
