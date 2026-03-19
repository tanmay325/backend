package com.etms.service;

import com.etms.entity.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(String userId, String title, String message, String type, String relatedId);
    List<Notification> getUserNotifications(String email);
    void markAsRead(String notificationId);
    void deleteNotification(String notificationId);
}
