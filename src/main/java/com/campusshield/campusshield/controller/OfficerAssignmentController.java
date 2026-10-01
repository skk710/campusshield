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
            @RequestParam Long incidentId,
            @RequestParam Long officerId,
            HttpSession session) {

        // Make sure the logged-in user is an officer
        String role = (String) session.getAttribute("userRole");

        if (role == null || !role.equals("OFFICER")) {
            return "redirect:/login";
        }

        // Find the incident
        Incident incident =
                incidentRepository.findById(incidentId).orElse(null);

        // Find the selected officer
        User officer =
                userRepository.findById(officerId).orElse(null);

        if (incident == null || officer == null) {
            return "redirect:/officer-dashboard";
        }

        // Create assignment
        OfficerAssignment assignment = new OfficerAssignment();

        assignment.setIncident(incident);
        assignment.setOfficer(officer);
        assignment.setAssignmentTime(LocalDateTime.now());
        assignment.setResponseNotes(null);

        // Save assignment
        assignmentRepository.save(assignment);

        // Update incident status
        incident.setStatus("ASSIGNED");
        incidentRepository.save(incident);

        return "redirect:/officer-dashboard";
    }
}