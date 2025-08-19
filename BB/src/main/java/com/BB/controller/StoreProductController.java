package com.BB.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BB.dto.StoreProductDTO;
import com.BB.service.StoreProductService;

@RestController
@RequestMapping("/api/store-products")
@CrossOrigin(origins = "*")
public class StoreProductController {
    
    @Autowired
    private StoreProductService storeProductService;
    
    @GetMapping
    public ResponseEntity<List<StoreProductDTO>> getAllStoreProducts() {
        List<StoreProductDTO> storeProducts = storeProductService.getAllStoreProducts();
        return ResponseEntity.ok(storeProducts);
    }
    
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StoreProductDTO>> getProductsByStore(@PathVariable Long storeId) {
        List<StoreProductDTO> products = storeProductService.getProductsByStore(storeId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StoreProductDTO>> getStoresByProduct(@PathVariable Long productId) {
        List<StoreProductDTO> stores = storeProductService.getStoresByProduct(productId);
        return ResponseEntity.ok(stores);
    }
    
    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<StoreProductDTO> getStoreProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        return storeProductService.getStoreProduct(storeId, productId)
                .map(storeProduct -> ResponseEntity.ok(storeProduct))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{storeId}/{productId}")
    public ResponseEntity<StoreProductDTO> addProductToStore(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam BigDecimal price,
            @RequestParam Integer quantity) {
        try {
            StoreProductDTO storeProduct = storeProductService.addProductToStore(storeId, productId, price, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(storeProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{storeId}/{productId}")
    public ResponseEntity<StoreProductDTO> updateStoreProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam BigDecimal price,
            @RequestParam Integer quantity) {
        return storeProductService.updateStoreProduct(storeId, productId, price, quantity)
                .map(storeProduct -> ResponseEntity.ok(storeProduct))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{storeId}/{productId}/stock")
    public ResponseEntity<StoreProductDTO> updateStock(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return storeProductService.updateStock(storeId, productId, quantity)
                .map(storeProduct -> ResponseEntity.ok(storeProduct))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<StoreProductDTO>> getAvailableProducts() {
        List<StoreProductDTO> products = storeProductService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<StoreProductDTO>> getProductsByCategory(@PathVariable String category) {
        List<StoreProductDTO> products = storeProductService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @DeleteMapping("/{storeId}/{productId}")
    public ResponseEntity<Void> removeProductFromStore(@PathVariable Long storeId, @PathVariable Long productId) {
        if (storeProductService.removeProductFromStore(storeId, productId)) {
        	System.out.println("new test 6");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}