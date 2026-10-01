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
public class OfficerDashboardController {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public OfficerDashboardController(
            IncidentRepository incidentRepository,
            UserRepository userRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/officer-dashboard")
    public String officerDashboard(
            HttpSession session,
            Model model) {

        String role = (String) session.getAttribute("userRole");

        if (role == null || !role.equals("OFFICER")) {
            return "redirect:/login";
        }

        List<Incident> incidents = incidentRepository.findAll();

        List<User> officers = userRepository.findByRole("OFFICER");

        String userName = (String) session.getAttribute("userName");

        model.addAttribute("userName", userName);
        model.addAttribute("incidents", incidents);
        model.addAttribute("officers", officers);

        return "officer-dashboard";
    }
}