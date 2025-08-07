package com.BB.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BB.dto.StoreDTO;
import com.BB.entity.Store;
import com.BB.repository.StoreRepository;

@Service
public class StoreService {
    
    @Autowired
    private StoreRepository storeRepository;
    
    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<StoreDTO> getStoreById(Long id) {
        return storeRepository.findById(id).map(this::convertToDTO);
    }
    
    public StoreDTO createStore(StoreDTO storeDTO) {
        Store store = convertToEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return convertToDTO(savedStore);
    }
    
    public Optional<StoreDTO> updateStore(Long id, StoreDTO storeDTO) {
        return storeRepository.findById(id).map(store -> {
            store.setName(storeDTO.getName());
            store.setAddress(storeDTO.getAddress());
            store.setCity(storeDTO.getCity());
            store.setState(storeDTO.getState());
            store.setZipCode(storeDTO.getZipCode());
            store.setPhoneNumber(storeDTO.getPhoneNumber());
            store.setManagerName(storeDTO.getManagerName());
            return convertToDTO(storeRepository.save(store));
        });
    }
    
    public boolean deleteStore(Long id) {
        if (storeRepository.existsById(id)) {
            storeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<StoreDTO> getStoresByCity(String city) {
        return storeRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private StoreDTO convertToDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setAddress(store.getAddress());
        dto.setCity(store.getCity());
        dto.setState(store.getState());
        dto.setZipCode(store.getZipCode());
        dto.setPhoneNumber(store.getPhoneNumber());
        dto.setManagerName(store.getManagerName());
        dto.setCreatedAt(store.getCreatedAt());
        return dto;
    }
    
    private Store convertToEntity(StoreDTO dto) {
        Store store = new Store();
        store.setName(dto.getName());
        store.setAddress(dto.getAddress());
        store.setCity(dto.getCity());
        store.setState(dto.getState());
        store.setZipCode(dto.getZipCode());
        store.setPhoneNumber(dto.getPhoneNumber());
        store.setManagerName(dto.getManagerName());
        return store;
    }
}