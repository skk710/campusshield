package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.IncidentRepository;
import com.campusshield.campusshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

@Controller
public class IncidentController {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentController(
            IncidentRepository incidentRepository,
            UserRepository userRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/report")
    public String showReportForm(
            HttpSession session) {

        // User must be logged in
        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        return "report-incident";
    }


    @PostMapping("/report")
    public String submitIncident(
            @ModelAttribute Incident incident,
            HttpSession session) {

        // Get logged-in user's ID
        Long userId =
                (Long) session.getAttribute("userId");

        // Prevent submitting without login
        if (userId == null) {
            return "redirect:/login";
        }


        // Find the logged-in user
        User user =
                userRepository
                        .findById(userId)
                        .orElse(null);


        // User no longer exists
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }


        // Set incident information
        incident.setReportedBy(user);

        incident.setStatus("REPORTED");

        incident.setCreatedAt(
                LocalDateTime.now()
        );


        // Save to PostgreSQL
        incidentRepository.save(incident);


        // Return to student dashboard
        return "redirect:/student-dashboard";
    }
}