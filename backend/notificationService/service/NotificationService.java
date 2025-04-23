package com.velixa.notificationservice.service;

import com.velixa.notificationservice.model.Notification;
import com.velixa.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        notification.setSent(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification sendNotification(Notification notification) {
        // Create a new notification
        notification.setSent(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send it
        return sendNotificationById(savedNotification.getId());
    }

    public Notification sendNotificationById(String id) {
        Notification notification = getNotificationById(id);
        
        // In a real application, this would connect to an email or SMS service
        // For this example, we're just simulating sending
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        
        System.out.println("Sending " + notification.getType() + " notification: " + 
                          notification.getSubject() + " to user: " + notification.getUserId());
        
        return notificationRepository.save(notification);
    }

    public Notification sendOrderConfirmation(String userId, String orderId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("EMAIL");
        notification.setSubject("Order Confirmation");
        notification.setContent("Your order #" + orderId + " has been confirmed and is being processed.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSent(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send the notification
        return sendNotificationById(savedNotification.getId());
    }
}
