package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeather(
            @RequestParam(defaultValue = "Chennai") String city,
            Model model) {

        try {

            String weatherData = weatherService.getWeather(city);

            double temperature = extractDouble(
                    weatherData,
                    "\"temp\":"
            );

            double feelsLike = extractDouble(
                    weatherData,
                    "\"feels_like\":"
            );

            double humidity = extractDouble(
                    weatherData,
                    "\"humidity\":"
            );

            double windSpeed = extractDouble(
                    weatherData,
                    "\"speed\":"
            );

            String condition = extractString(
                    weatherData,
                    "\"description\":\""
            );

            String message = generateMessage(
                    temperature,
                    feelsLike,
                    condition,
                    windSpeed
            );

            String icon = getWeatherIcon(condition);

            model.addAttribute("city", city);
            model.addAttribute("temperature", temperature);
            model.addAttribute("feelsLike", feelsLike);
            model.addAttribute("humidity", humidity);
            model.addAttribute("windSpeed", windSpeed);
            model.addAttribute("condition", capitalize(condition));
            model.addAttribute("message", message);
            model.addAttribute("icon", icon);
            model.addAttribute("weatherAvailable", true);

        } catch (Exception e) {

            model.addAttribute("city", city);
            model.addAttribute("weatherAvailable", false);
            model.addAttribute(
                    "weatherError",
                    "Couldn't fetch weather right now."
            );
        }

        return "weather";
    }

    private double extractDouble(String json, String key) {

        Pattern pattern = Pattern.compile(
                Pattern.quote(key) + "(-?[0-9]+(?:\\.[0-9]+)?)"
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        throw new RuntimeException(
                "Weather value not found: " + key
        );
    }

    private String extractString(String json, String key) {

        Pattern pattern = Pattern.compile(
                Pattern.quote(key) + "([^\"]+)"
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    private String capitalize(String text) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        return Character.toUpperCase(text.charAt(0))
                + text.substring(1);
    }

    private String getWeatherIcon(String condition) {

        condition = condition.toLowerCase();

        if (condition.contains("rain")) {
            return "🌧️";
        }

        if (condition.contains("thunderstorm")) {
            return "⛈️";
        }

        if (condition.contains("cloud")) {
            return "☁️";
        }

        if (condition.contains("clear")) {
            return "☀️";
        }

        if (condition.contains("snow")) {
            return "❄️";
        }

        return "🌤️";
    }

    private String generateMessage(
            double temperature,
            double feelsLike,
            String condition,
            double windSpeed) {

        String lowerCondition = condition.toLowerCase();

        if (lowerCondition.contains("thunderstorm")) {
            return "Sky said stay indoors. Maybe listen to it. ⛈️";
        }

        if (lowerCondition.contains("rain")) {
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

        if (lowerCondition.contains("cloud")) {
            return "The sky looks suspicious today... ☁️👀";
        }

        if (lowerCondition.contains("clear")) {
            return "Campus weather understood the assignment. 😎☀️";
        }

        return "Weather's looking decent. Go touch some grass. 🌤️😂";
    }
}