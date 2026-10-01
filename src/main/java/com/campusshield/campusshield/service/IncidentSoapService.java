package com.campusshield.campusshield.service;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.OfficerAssignment;
import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.IncidentRepository;
import com.campusshield.campusshield.repository.OfficerAssignmentRepository;
import com.campusshield.campusshield.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentSoapService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final OfficerAssignmentRepository assignmentRepository;

    public IncidentSoapService(
            IncidentRepository incidentRepository,
            UserRepository userRepository,
            OfficerAssignmentRepository assignmentRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // =========================================================
    // REPORT INCIDENT
    // =========================================================

    public Incident reportIncident(
            Long userId,
            String category,
            String description,
            String location,
            String severity) {

        User user =
                userRepository
                        .findById(userId)
                        .orElse(null);

        if (user == null) {
            return null;
        }

        Incident incident = new Incident();

        incident.setReportedBy(user);
        incident.setCategory(category);
        incident.setDescription(description);
        incident.setLocation(location);
        incident.setSeverity(severity);
        incident.setStatus("REPORTED");
        incident.setCreatedAt(LocalDateTime.now());

        return incidentRepository.save(incident);
    }


    // =========================================================
    // GET INCIDENT BY ID
    // =========================================================

    public Incident getIncidentById(Long incidentId) {

        return incidentRepository
                .findById(incidentId)
                .orElse(null);
    }


    // =========================================================
    // GET STUDENT INCIDENTS
    // =========================================================

    public List<Incident> getStudentIncidents(Long userId) {

        return incidentRepository
                .findByReportedBy_UserId(userId);
    }


    // =========================================================
    // UPDATE INCIDENT STATUS
    // =========================================================

    public Incident updateIncidentStatus(
            Long incidentId,
            String newStatus) {

        Incident incident =
                incidentRepository
                        .findById(incidentId)
                        .orElse(null);

        if (incident == null) {
            return null;
        }

        String currentStatus = incident.getStatus();

        boolean validTransition = false;

        if (currentStatus.equals("REPORTED")
                && newStatus.equals("ASSIGNED")) {

            validTransition = true;

        } else if (currentStatus.equals("ASSIGNED")
                && newStatus.equals("IN_PROGRESS")) {

            validTransition = true;

        } else if (currentStatus.equals("IN_PROGRESS")
                && newStatus.equals("RESOLVED")) {

            validTransition = true;

        } else if (currentStatus.equals("RESOLVED")
                && newStatus.equals("CLOSED")) {

            validTransition = true;
        }

        if (!validTransition) {
            return null;
        }

        incident.setStatus(newStatus);

        return incidentRepository.save(incident);
    }


    // =========================================================
    // ASSIGN OFFICER
    // =========================================================

    public OfficerAssignment assignOfficer(
            Long incidentId,
            Long officerId) {

        Incident incident =
                incidentRepository
                        .findById(incidentId)
                        .orElse(null);

        User officer =
                userRepository
                        .findById(officerId)
                        .orElse(null);

        if (incident == null || officer == null) {
            return null;
        }

        if (!"OFFICER".equals(officer.getRole())) {
            return null;
        }

        if (!"REPORTED".equals(incident.getStatus())) {
            return null;
        }

        OfficerAssignment assignment =
                new OfficerAssignment();

        assignment.setIncident(incident);
        assignment.setOfficer(officer);
        assignment.setAssignmentTime(LocalDateTime.now());
        assignment.setResponseNotes(null);

        OfficerAssignment savedAssignment =
                assignmentRepository.save(assignment);

        incident.setStatus("ASSIGNED");

        incidentRepository.save(incident);

        return savedAssignment;
    }


    // =========================================================
    // INCIDENT STATISTICS
    // =========================================================

    public long getTotalIncidents() {
        return incidentRepository.count();
    }

    public long getReportedIncidents() {
        return incidentRepository.countByStatus("REPORTED");
    }

    public long getAssignedIncidents() {
        return incidentRepository.countByStatus("ASSIGNED");
    }

    public long getInProgressIncidents() {
        return incidentRepository.countByStatus("IN_PROGRESS");
    }

    public long getResolvedIncidents() {
        return incidentRepository.countByStatus("RESOLVED");
    }

    public long getClosedIncidents() {
        return incidentRepository.countByStatus("CLOSED");
    }

    public long getHighSeverityIncidents() {
        return incidentRepository.countBySeverity("HIGH");
    }

    public long getCriticalSeverityIncidents() {
        return incidentRepository.countBySeverity("CRITICAL");
    }
}