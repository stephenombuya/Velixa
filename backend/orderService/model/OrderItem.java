// File: order-service/src/main/java/com/velixa/orderservice/model/OrderItem.java (continued)
package com.velixa.orderservice.model;

import java.math.BigDecimal;

public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    // Getters and setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
