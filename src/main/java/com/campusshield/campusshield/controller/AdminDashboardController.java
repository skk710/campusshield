package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.IncidentRepository;
import com.campusshield.campusshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminDashboardController {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(
            IncidentRepository incidentRepository,
            UserRepository userRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        String role =
                (String) session.getAttribute("userRole");

        if (role == null || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        // All incidents reported by students
        List<Incident> incidents =
                incidentRepository.findAll();

        // All registered officers
        List<User> officers =
                userRepository.findByRole("OFFICER");

        // Statistics
        long totalIncidents =
                incidentRepository.count();

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

        long highSeverity =
                incidentRepository.countBySeverity("HIGH");

        long criticalSeverity =
                incidentRepository.countBySeverity("CRITICAL");

        model.addAttribute(
                "incidents",
                incidents
        );

        model.addAttribute(
                "officers",
                officers
        );

        model.addAttribute(
                "totalIncidents",
                totalIncidents
        );

        model.addAttribute(
                "reported",
                reported
        );

        model.addAttribute(
                "assigned",
                assigned
        );

        model.addAttribute(
                "inProgress",
                inProgress
        );

        model.addAttribute(
                "resolved",
                resolved
        );

        model.addAttribute(
                "closed",
                closed
        );

        model.addAttribute(
                "highSeverity",
                highSeverity
        );

        model.addAttribute(
                "criticalSeverity",
                criticalSeverity
        );

        model.addAttribute(
                "userName",
                session.getAttribute("userName")
        );

        return "admin-dashboard";
    }
}