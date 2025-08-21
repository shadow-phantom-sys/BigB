package com.BB.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.BB.dto.StoreProductDTO;
import com.BB.service.StoreProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(StoreProductController.class)
class StoreProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreProductService storeProductService;

    @Autowired
    private ObjectMapper objectMapper;

    private StoreProductDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new StoreProductDTO();
        sampleDto.setStoreId(1L);
        sampleDto.setProductId(10L);
        sampleDto.setPrice(BigDecimal.valueOf(100));
        sampleDto.setQuantity(5);
    }

    @Test
    void testGetAllStoreProducts() throws Exception {
        when(storeProductService.getAllStoreProducts()).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/store-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeId").value(1L))
                .andExpect(jsonPath("$[0].productId").value(10L));
    }

    @Test
    void testGetProductsByStore() throws Exception {
        when(storeProductService.getProductsByStore(1L)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/store-products/store/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(10L));
    }

    @Test
    void testGetStoresByProduct() throws Exception {
        when(storeProductService.getStoresByProduct(10L)).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/store-products/product/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeId").value(1L));
    }

    @Test
    void testGetStoreProductFound() throws Exception {
        when(storeProductService.getStoreProduct(1L, 10L)).thenReturn(Optional.of(sampleDto));

        mockMvc.perform(get("/api/store-products/1/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(1L));
    }

    @Test
    void testGetStoreProductNotFound() throws Exception {
        when(storeProductService.getStoreProduct(1L, 10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/store-products/1/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddProductToStoreSuccess() throws Exception {
        when(storeProductService.addProductToStore(1L, 10L, BigDecimal.valueOf(100), 5))
                .thenReturn(sampleDto);

        mockMvc.perform(post("/api/store-products/1/10")
                        .param("price", "100")
                        .param("quantity", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void testAddProductToStoreFailure() throws Exception {
        when(storeProductService.addProductToStore(anyLong(), anyLong(), any(), anyInt()))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/store-products/1/10")
                        .param("price", "100")
                        .param("quantity", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStoreProductFound() throws Exception {
        when(storeProductService.updateStoreProduct(1L, 10L, BigDecimal.valueOf(200), 8))
                .thenReturn(Optional.of(sampleDto));

        mockMvc.perform(put("/api/store-products/1/10")
                        .param("price", "200")
                        .param("quantity", "8"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStoreProductNotFound() throws Exception {
        when(storeProductService.updateStoreProduct(1L, 10L, BigDecimal.valueOf(200), 8))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/store-products/1/10")
                        .param("price", "200")
                        .param("quantity", "8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStockFound() throws Exception {
        when(storeProductService.updateStock(1L, 10L, 15))
                .thenReturn(Optional.of(sampleDto));

        mockMvc.perform(patch("/api/store-products/1/10/stock")
                        .param("quantity", "15"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStockNotFound() throws Exception {
        when(storeProductService.updateStock(1L, 10L, 15))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/store-products/1/10/stock")
                        .param("quantity", "15"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAvailableProducts() throws Exception {
        when(storeProductService.getAvailableProducts()).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/store-products/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(10L));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        when(storeProductService.getProductsByCategory("Electronics")).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/store-products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(10L));
    }

    @Test
    void testRemoveProductFromStoreFound() throws Exception {
        when(storeProductService.removeProductFromStore(1L, 10L)).thenReturn(true);

        mockMvc.perform(delete("/api/store-products/1/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testRemoveProductFromStoreNotFound() throws Exception {
        when(storeProductService.removeProductFromStore(1L, 10L)).thenReturn(false);

        mockMvc.perform(delete("/api/store-products/1/10"))
                .andExpect(status().isNotFound());
    }
}