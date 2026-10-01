package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.repository.IncidentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IncidentStatusController {

    private final IncidentRepository incidentRepository;

    public IncidentStatusController(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long incidentId,
            @RequestParam String status,
            HttpSession session) {

        // Only officers can update incident status
        String role = (String) session.getAttribute("userRole");

        if (role == null || !role.equals("OFFICER")) {
            return "redirect:/login";
        }

        Incident incident =
                incidentRepository.findById(incidentId).orElse(null);

        if (incident == null) {
            return "redirect:/officer-dashboard";
        }

        String currentStatus = incident.getStatus();

        // Check whether the requested transition is valid
        boolean validTransition = false;

        if (currentStatus.equals("REPORTED")
                && status.equals("ASSIGNED")) {

            validTransition = true;

        } else if (currentStatus.equals("ASSIGNED")
                && status.equals("IN_PROGRESS")) {

            validTransition = true;

        } else if (currentStatus.equals("IN_PROGRESS")
                && status.equals("RESOLVED")) {

            validTransition = true;

        } else if (currentStatus.equals("RESOLVED")
                && status.equals("CLOSED")) {

            validTransition = true;
        }

        // Update only if the transition is valid
        if (validTransition) {

            incident.setStatus(status);

            incidentRepository.save(incident);
        }

        return "redirect:/officer-dashboard";
    }
}