package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.OfficerAssignment;
import com.campusshield.campusshield.repository.OfficerAssignmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OfficerDashboardController {

    private final OfficerAssignmentRepository assignmentRepository;

    public OfficerDashboardController(
            OfficerAssignmentRepository assignmentRepository) {

        this.assignmentRepository = assignmentRepository;
    }

    @GetMapping("/officer-dashboard")
    public String officerDashboard(
            HttpSession session,
            Model model) {

        String role =
                (String) session.getAttribute("userRole");

        // Only OFFICER can access this dashboard.
        if (role == null || !"OFFICER".equals(role)) {
            return "redirect:/login";
        }

        Long officerId =
                (Long) session.getAttribute("userId");

        if (officerId == null) {
            return "redirect:/login";
        }

        /*
         * Get assignments belonging to
         * the currently logged-in officer.
         */
        List<OfficerAssignment> assignments =
                assignmentRepository
                        .findByOfficer_UserId(officerId);

        /*
         * Use a LinkedHashMap so that:
         *
         * 1. The original order is preserved.
         * 2. The same incident cannot appear twice.
         */
        Map<Long, Incident> uniqueIncidents =
                new LinkedHashMap<>();

        for (OfficerAssignment assignment : assignments) {

            if (assignment == null) {
                continue;
            }

            Incident incident =
                    assignment.getIncident();

            if (incident == null) {
                continue;
            }

            Long incidentId =
                    incident.getIncidentId();

            if (incidentId == null) {
                continue;
            }

            uniqueIncidents.putIfAbsent(
                    incidentId,
                    incident
            );
        }

        List<Incident> incidents =
                new ArrayList<>(
                        uniqueIncidents.values()
                );

        model.addAttribute(
                "userName",
                session.getAttribute("userName")
        );

        model.addAttribute(
                "incidents",
                incidents
        );

        return "officer-dashboard";
    }
}