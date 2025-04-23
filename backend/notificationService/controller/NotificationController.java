package com.velixa.notificationservice.controller;

import com.velixa.notificationservice.model.Notification;
import com.velixa.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification newNotification = notificationService.createNotification(notification);
        return new ResponseEntity<>(newNotification, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable String id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Notification notification) {
        try {
            Notification sentNotification = notificationService.sendNotification(notification);
            return new ResponseEntity<>(sentNotification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<?> sendNotificationById(@PathVariable String id) {
        try {
            Notification sentNotification = notificationService.sendNotificationById(id);
            return new ResponseEntity<>(sentNotification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/order-confirmation")
    public ResponseEntity<?> sendOrderConfirmation(@RequestParam String userId, 
                                                  @RequestParam String orderId) {
        try {
            Notification notification = notificationService.sendOrderConfirmation(userId, orderId);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
