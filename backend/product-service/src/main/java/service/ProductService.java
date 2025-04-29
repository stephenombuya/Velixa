package com.velixa.product.service;

import com.velixa.product.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    Optional<ProductDTO> getProductById(String id);
    List<ProductDTO> getAllProducts();
    Optional<ProductDTO> updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
}
