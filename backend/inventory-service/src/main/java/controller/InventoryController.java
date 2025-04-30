package com.velixa.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velixa.inventory.model.Inventory;
import com.velixa.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String productId) {
        return inventoryService.getInventoryByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{productId}/{quantity}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable String productId, @PathVariable int quantity) {
        return new ResponseEntity<>(inventoryService.updateInventory(productId, quantity), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        return new ResponseEntity<>(inventoryService.createInventory(inventory), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String productId) {
        boolean deleted = inventoryService.deleteInventory(productId);
        return deleted ? 
                new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/{productId}/available/{quantity}")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String productId, @PathVariable int quantity) {
        boolean available = inventoryService.isInStock(productId, quantity);
        return ResponseEntity.ok(available);
    }
    
    @PutMapping("/{productId}")
    public ResponseEntity<Inventory> replaceInventory(@PathVariable String productId, @RequestBody Inventory inventory) {
        // Ensure the path variable and body ID match
        inventory.setProductId(productId);
        return new ResponseEntity<>(inventoryService.createInventory(inventory), HttpStatus.OK);
    }
}
