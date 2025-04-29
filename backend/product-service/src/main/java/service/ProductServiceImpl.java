package com.velixa.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velixa.product.dto.ProductDTO;
import com.velixa.product.model.Product;
import com.velixa.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
	@Autowired
	private ProductRepository productRepository;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        
        product = productRepository.save(product);
        productDTO.setId(product.getId());
        return productDTO;
    }

    @Override
    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(id)
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity()));
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDTO> updateProduct(String id, ProductDTO productDTO) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(productDTO.getName());
            existing.setDescription(productDTO.getDescription());
            existing.setPrice(productDTO.getPrice());
            existing.setQuantity(productDTO.getQuantity());
            Product updated = productRepository.save(existing);
            return new ProductDTO(updated.getId(), updated.getName(), updated.getDescription(), updated.getPrice(), updated.getQuantity());
        });
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
