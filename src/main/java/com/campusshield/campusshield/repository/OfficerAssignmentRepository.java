package com.campusshield.campusshield.repository;

import com.campusshield.campusshield.entity.OfficerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficerAssignmentRepository
        extends JpaRepository<OfficerAssignment, Long> {

    List<OfficerAssignment> findByOfficer_UserId(Long userId);

    List<OfficerAssignment> findByIncident_IncidentId(Long incidentId);
}