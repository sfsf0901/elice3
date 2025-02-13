package com.example.elice_3rd.notification.repository;

import com.example.elice_3rd.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdAndReadStatus(Long receiverId, Boolean readStatus);

}
