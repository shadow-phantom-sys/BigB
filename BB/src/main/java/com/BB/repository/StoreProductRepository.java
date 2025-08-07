package com.BB.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.BB.entity.StoreProduct;

@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, Long> {
    List<StoreProduct> findByStoreId(Long storeId);
    List<StoreProduct> findByProductId(Long productId);
    Optional<StoreProduct> findByStoreIdAndProductId(Long storeId, Long productId);
    
    @Query("SELECT sp FROM StoreProduct sp WHERE sp.isAvailable = true AND sp.quantity > 0")
    List<StoreProduct> findAvailableProducts();
    
    @Query("SELECT sp FROM StoreProduct sp WHERE sp.store.id = :storeId AND sp.isAvailable = true AND sp.quantity > 0")
    List<StoreProduct> findAvailableProductsByStore(@Param("storeId") Long storeId);
    
    @Query("SELECT sp FROM StoreProduct sp WHERE sp.product.category = :category AND sp.isAvailable = true")
    List<StoreProduct> findByCategory(@Param("category") String category);
}