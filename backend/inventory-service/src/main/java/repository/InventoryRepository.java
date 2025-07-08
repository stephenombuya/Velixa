package com.velixa.inventory.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.velixa.inventory.model.Inventory;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    
    /**
     * Find inventory by product ID
     * 
     * @param productId the ID of the product to find
     * @return Optional containing the inventory if found, empty otherwise
     */
    Optional<Inventory> findByProductId(String productId);
    
    /**
     * Find inventories with quantity less than the specified value
     * 
     * @param quantity the threshold quantity
     * @return List of inventories with quantity less than the threshold
     */
    java.util.List<Inventory> findByQuantityLessThan(int quantity);
    
    /**
     * Find inventories by warehouse location
     * 
     * @param location the warehouse location to search for
     * @return List of inventories in the specified location
     */
    java.util.List<Inventory> findByWarehouseLocation(String location);
}
