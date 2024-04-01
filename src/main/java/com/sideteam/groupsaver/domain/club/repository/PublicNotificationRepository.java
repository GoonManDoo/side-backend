package com.sideteam.groupsaver.domain.club.repository;

import com.sideteam.groupsaver.domain.club.domain.PublicNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PublicNotificationRepository extends JpaRepository<PublicNotification, Long> {
    List<PublicNotification> findAllByClubId(long clubId);
}
