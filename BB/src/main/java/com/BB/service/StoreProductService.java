package com.BB.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BB.dto.StoreProductDTO;
import com.BB.entity.Product;
import com.BB.entity.Store;
import com.BB.entity.StoreProduct;
import com.BB.repository.ProductRepository;
import com.BB.repository.StoreProductRepository;
import com.BB.repository.StoreRepository;

@Service
public class StoreProductService {
    
    @Autowired
    private StoreProductRepository storeProductRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<StoreProductDTO> getAllStoreProducts() {
        return storeProductRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StoreProductDTO> getProductsByStore(Long storeId) {
        return storeProductRepository.findByStoreId(storeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StoreProductDTO> getStoresByProduct(Long productId) {
        return storeProductRepository.findByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<StoreProductDTO> getStoreProduct(Long storeId, Long productId) {
        return storeProductRepository.findByStoreIdAndProductId(storeId, productId)
                .map(this::convertToDTO);
    }
    
    public StoreProductDTO addProductToStore(Long storeId, Long productId, BigDecimal price, Integer quantity) {
        Optional<Store> store = storeRepository.findById(storeId);
        Optional<Product> product = productRepository.findById(productId);
        
        if (store.isPresent() && product.isPresent()) {
            StoreProduct storeProduct = new StoreProduct(store.get(), product.get(), price, quantity);
            storeProduct.setLastRestocked(LocalDateTime.now());
            StoreProduct saved = storeProductRepository.save(storeProduct);
            return convertToDTO(saved);
        }
        throw new RuntimeException("Store or Product not found");
    }
    
    public Optional<StoreProductDTO> updateStoreProduct(Long storeId, Long productId, BigDecimal price, Integer quantity) {
        return storeProductRepository.findByStoreIdAndProductId(storeId, productId)
                .map(storeProduct -> {
                    storeProduct.setPrice(price);
                    storeProduct.setQuantity(quantity);
                    return convertToDTO(storeProductRepository.save(storeProduct));
                });
    }
    
    public Optional<StoreProductDTO> updateStock(Long storeId, Long productId, Integer newQuantity) {
        return storeProductRepository.findByStoreIdAndProductId(storeId, productId)
                .map(storeProduct -> {
                    storeProduct.setQuantity(newQuantity);
                    storeProduct.setLastRestocked(LocalDateTime.now());
                    storeProduct.setIsAvailable(newQuantity > 0);
                    return convertToDTO(storeProductRepository.save(storeProduct));
                });
    }
    
    public List<StoreProductDTO> getAvailableProducts() {
        return storeProductRepository.findAvailableProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StoreProductDTO> getProductsByCategory(String category) {
        return storeProductRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public boolean removeProductFromStore(Long storeId, Long productId) {
        Optional<StoreProduct> storeProduct = storeProductRepository.findByStoreIdAndProductId(storeId, productId);
        if (storeProduct.isPresent()) {
            storeProductRepository.delete(storeProduct.get());
            return true;
        }
        return false;
    }
    
    private StoreProductDTO convertToDTO(StoreProduct storeProduct) {
        StoreProductDTO dto = new StoreProductDTO();
        dto.setId(storeProduct.getId());
        dto.setStoreId(storeProduct.getStore().getId());
        dto.setStoreName(storeProduct.getStore().getName());
        dto.setProductId(storeProduct.getProduct().getId());
        dto.setProductName(storeProduct.getProduct().getName());
        dto.setProductCategory(storeProduct.getProduct().getCategory());
        dto.setProductBrand(storeProduct.getProduct().getBrand());
        dto.setPrice(storeProduct.getPrice());
        dto.setDiscountedPrice(storeProduct.getDiscountedPrice());
        dto.setQuantity(storeProduct.getQuantity());
        dto.setDiscountPercentage(storeProduct.getDiscountPercentage());
        dto.setIsAvailable(storeProduct.getIsAvailable());
        dto.setLastRestocked(storeProduct.getLastRestocked());
        return dto;
    }
}