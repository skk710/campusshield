package com.campusshield.campusshield.repository;

import com.campusshield.campusshield.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByReportedBy_UserId(Long userId);

    long countByStatus(String status);

    long countBySeverity(String severity);
}