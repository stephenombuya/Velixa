package com.velixa.cartservice.service;

import com.velixa.cartservice.model.Cart;
import com.velixa.cartservice.model.CartItem;
import com.velixa.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    public Cart createCart(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart addItemToCart(String userId, CartItem item) {
        Cart cart = getOrCreateCart(userId);
        
        // Check if the product already exists in the cart
        Optional<CartItem> existingItem = cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity if product already exists
            existingItem.get().setQuantity(existingItem.get().getQuantity() + item.getQuantity());
        } else {
            // Add new item to cart
            cart.getItems().add(item);
        }
        
        // Recalculate total price
        updateCartTotals(cart);
        
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String userId, String productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        
        // Find the cart item
        Optional<CartItem> existingItem = cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
        
        if (!existingItem.isPresent()) {
            throw new RuntimeException("Product not found in cart");
        }
        
        // Update quantity
        existingItem.get().setQuantity(quantity);
        
        // Remove item if quantity is 0
        if (quantity <= 0) {
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        }
        
        // Recalculate total price
        updateCartTotals(cart);
        
        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(String userId, String productId) {
        Cart cart = getCartByUserId(userId);
        
        boolean removed = cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        
        if (!removed) {
            throw new RuntimeException("Product not found in cart");
        }
        
        // Recalculate total price
        updateCartTotals(cart);
        
        return cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(String userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            return createCart(userId);
        }
    }

    private void updateCartTotals(Cart cart) {
        BigDecimal total = cart.getItems()
                .stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
    }
}
