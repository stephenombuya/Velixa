package com.velixa.inventory.service;

import java.util.Optional;
import com.velixa.inventory.model.Inventory;

public interface InventoryService {
    /**
     * Retrieves inventory information by product ID
     * 
     * @param productId the ID of the product to look up
     * @return Optional containing the inventory if found, empty otherwise
     */
    Optional<Inventory> getInventoryByProductId(String productId);
    
    /**
     * Updates the inventory quantity for a product
     * 
     * @param productId the ID of the product to update
     * @param quantity the new quantity to set
     * @return the updated Inventory entity
     */
    Inventory updateInventory(String productId, int quantity);
    
    /**
     * Creates a new inventory record
     * 
     * @param inventory the inventory entity to save
     * @return the created Inventory entity
     */
    Inventory createInventory(Inventory inventory);
    
    /**
     * Deletes an inventory record by product ID
     * 
     * @param productId the ID of the product to delete
     * @return true if deleted, false if not found
     */
    boolean deleteInventory(String productId);
    
    /**
     * Checks if a product is in stock
     * 
     * @param productId the ID of the product to check
     * @param requiredQuantity the quantity required
     * @return true if the product is available in the required quantity
     */
    boolean isInStock(String productId, int requiredQuantity);
}
