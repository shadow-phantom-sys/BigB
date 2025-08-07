package com.BB.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BB.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByCity(String city);
    List<Store> findByState(String state);
    List<Store> findByNameContainingIgnoreCase(String name);
}