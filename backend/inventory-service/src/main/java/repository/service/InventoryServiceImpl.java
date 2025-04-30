package com.velixa.inventory.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velixa.inventory.model.Inventory;
import com.velixa.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Override
    public Optional<Inventory> getInventoryByProductId(String productId) {
        return inventoryRepository.findByProductId(productId);
    }
    
    @Override
    public Inventory updateInventory(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(new Inventory());
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        
        // Update status based on quantity
        if (quantity <= 0) {
            inventory.setStatus("OUT_OF_STOCK");
        } else if (quantity < 10) {
            inventory.setStatus("LOW_STOCK");
        } else {
            inventory.setStatus("IN_STOCK");
        }
        
        // Set the last updated timestamp
        inventory.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        return inventoryRepository.save(inventory);
    }
    
    @Override
    public Inventory createInventory(Inventory inventory) {
        // Set default values if not provided
        if (inventory.getStatus() == null) {
            if (inventory.getQuantity() <= 0) {
                inventory.setStatus("OUT_OF_STOCK");
            } else if (inventory.getQuantity() < 10) {
                inventory.setStatus("LOW_STOCK");
            } else {
                inventory.setStatus("IN_STOCK");
            }
        }
        
        // Set the last updated timestamp
        inventory.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        return inventoryRepository.save(inventory);
    }
    
    @Override
    public boolean deleteInventory(String productId) {
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            inventoryRepository.deleteById(productId);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isInStock(String productId, int requiredQuantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            return inventory.getQuantity() >= requiredQuantity;
        }
        return false;
    }
}
