package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.Incident;
import com.campusshield.campusshield.repository.IncidentRepository;
import com.campusshield.campusshield.service.WeatherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class StudentDashboardController {

    private final IncidentRepository incidentRepository;
    private final WeatherService weatherService;

    public StudentDashboardController(
            IncidentRepository incidentRepository,
            WeatherService weatherService) {

        this.incidentRepository = incidentRepository;
        this.weatherService = weatherService;
    }

    @GetMapping("/student-dashboard")
    public String studentDashboard(
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        String userName =
                (String) session.getAttribute("userName");

        List<Incident> incidents =
                incidentRepository.findByReportedBy_UserId(userId);

        model.addAttribute("userName", userName);
        model.addAttribute("incidents", incidents);

        // ---------------- WEATHER ----------------

        try {

            String city = "Chennai";

            String weatherData =
                    weatherService.getWeather(city);

            double temperature =
                    extractDouble(weatherData, "\"temp\":");

            double feelsLike =
                    extractDouble(weatherData, "\"feels_like\":");

            double humidity =
                    extractDouble(weatherData, "\"humidity\":");

            double windSpeed =
                    extractDouble(weatherData, "\"speed\":");

            String condition =
                    extractString(weatherData, "\"description\":\"");

            String weatherIcon =
                    getWeatherIcon(condition);

            String weatherMessage =
                    generateWeatherMessage(
                            temperature,
                            feelsLike,
                            condition,
                            windSpeed
                    );

            model.addAttribute("weatherCity", city);
            model.addAttribute("weatherTemperature", temperature);
            model.addAttribute("weatherFeelsLike", feelsLike);
            model.addAttribute("weatherHumidity", humidity);
            model.addAttribute("weatherWindSpeed", windSpeed);
            model.addAttribute(
                    "weatherCondition",
                    capitalize(condition)
            );
            model.addAttribute(
                    "weatherIcon",
                    weatherIcon
            );
            model.addAttribute(
                    "weatherMessage",
                    weatherMessage
            );
            model.addAttribute(
                    "weatherAvailable",
                    true
            );

        } catch (Exception e) {

            model.addAttribute(
                    "weatherAvailable",
                    false
            );

            model.addAttribute(
                    "weatherError",
                    "Weather information is temporarily unavailable."
            );
        }

        return "student-dashboard";
    }

    private double extractDouble(
            String json,
            String key) {

        Pattern pattern = Pattern.compile(
                Pattern.quote(key)
                        + "(-?[0-9]+(?:\\.[0-9]+)?)"
        );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        throw new RuntimeException(
                "Weather value not found: " + key
        );
    }

    private String extractString(
            String json,
            String key) {

        Pattern pattern = Pattern.compile(
                Pattern.quote(key)
                        + "([^\"]+)"
        );

        Matcher matcher =
                pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    private String capitalize(String text) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        return Character.toUpperCase(
                text.charAt(0)
        ) + text.substring(1);
    }

    private String getWeatherIcon(
            String condition) {

        String lower =
                condition.toLowerCase();

        if (lower.contains("thunderstorm")) {
            return "⛈️";
        }

        if (lower.contains("rain")) {
            return "🌧️";
        }

        if (lower.contains("cloud")) {
            return "☁️";
        }

        if (lower.contains("clear")) {
            return "☀️";
        }

        if (lower.contains("snow")) {
            return "❄️";
        }

        return "🌤️";
    }

    private String generateWeatherMessage(
            double temperature,
            double feelsLike,
            String condition,
            double windSpeed) {

        String lower =
                condition.toLowerCase();

        if (lower.contains("thunderstorm")) {
            return "Sky said stay indoors. Maybe listen to it. ⛈️";
        }

        if (lower.contains("rain")) {
            return "Rain's on the way. Don't get caught lacking. ☔";
        }

        if (feelsLike >= 40) {
            return "Sun chose violence today. 🥵🔥 Grab water + an umbrella.";
        }

        if (temperature >= 35) {
            return "Chennai is cooking today. 🔥 Carry some water!";
        }

        if (windSpeed >= 10) {
            return "The wind has entered the chat. Hold onto your stuff. 💨";
        }

        if (lower.contains("cloud")) {
            return "The sky looks suspicious today... ☁️👀";
        }

        if (lower.contains("clear")) {
            return "Campus weather understood the assignment. 😎☀️";
        }

        return "Weather's looking decent. Go touch some grass. 🌤️😂";
    }
}