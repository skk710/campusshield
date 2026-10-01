package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.repository.IncidentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final IncidentRepository incidentRepository;

    public AdminDashboardController(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        String role = (String) session.getAttribute("userRole");

        // Only ADMIN users can access this dashboard
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/login";
        }

        // Total incidents
        long totalIncidents = incidentRepository.count();

        // Incident status statistics
        long reported =
                incidentRepository.countByStatus("REPORTED");

        long assigned =
                incidentRepository.countByStatus("ASSIGNED");

        long inProgress =
                incidentRepository.countByStatus("IN_PROGRESS");

        long resolved =
                incidentRepository.countByStatus("RESOLVED");

        long closed =
                incidentRepository.countByStatus("CLOSED");

        // Incident severity statistics
        long highSeverity =
                incidentRepository.countBySeverity("HIGH");

        long criticalSeverity =
                incidentRepository.countBySeverity("CRITICAL");

        // Send data to Thymeleaf
        model.addAttribute("totalIncidents", totalIncidents);

        model.addAttribute("reported", reported);
        model.addAttribute("assigned", assigned);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("resolved", resolved);
        model.addAttribute("closed", closed);

        model.addAttribute("highSeverity", highSeverity);
        model.addAttribute("criticalSeverity", criticalSeverity);

        model.addAttribute(
                "userName",
                session.getAttribute("userName")
        );

        return "admin-dashboard";
    }
}