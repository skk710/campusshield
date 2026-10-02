package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.OfficerAssignment;
import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.IncidentRepository;
import com.campusshield.campusshield.repository.OfficerAssignmentRepository;
import com.campusshield.campusshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OfficerAssignmentController {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final OfficerAssignmentRepository assignmentRepository;

    public OfficerAssignmentController(
            IncidentRepository incidentRepository,
            UserRepository userRepository,
            OfficerAssignmentRepository assignmentRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @PostMapping("/assign-officer")
    public String assignOfficer(
            @RequestParam("incidentId") Long incidentId,
            @RequestParam("officerId") Long officerId,
            HttpSession session) {

        String role =
                (String) session.getAttribute("userRole");

        // Only ADMIN can assign officers.
        if (role == null || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        Incident incident =
                incidentRepository
                        .findById(incidentId)
                        .orElse(null);

        User officer =
                userRepository
                        .findById(officerId)
                        .orElse(null);

        if (incident == null || officer == null) {
            return "redirect:/admin-dashboard";
        }

        // Selected user must actually be an officer.
        if (!"OFFICER".equals(officer.getRole())) {
            return "redirect:/admin-dashboard";
        }

        // Only REPORTED incidents can receive a new assignment.
        if (!"REPORTED".equals(incident.getStatus())) {
            return "redirect:/admin-dashboard";
        }

        /*
         * Prevent duplicate assignment.
         *
         * If this incident already has an assignment,
         * do not create another one.
         */
        List<OfficerAssignment> existingAssignments =
                assignmentRepository
                        .findByIncident_IncidentId(incidentId);

        if (existingAssignments != null
                && !existingAssignments.isEmpty()) {

            return "redirect:/admin-dashboard";
        }

        OfficerAssignment assignment =
                new OfficerAssignment();

        assignment.setIncident(incident);
        assignment.setOfficer(officer);
        assignment.setAssignmentTime(
                LocalDateTime.now()
        );
        assignment.setResponseNotes(null);

        assignmentRepository.save(assignment);

        incident.setStatus("ASSIGNED");

        incidentRepository.save(incident);

        return "redirect:/admin-dashboard";
    }
}