package com.campusshield.campusshield.controller;

import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {

        // ADMIN accounts cannot be created through public registration.
        if (!role.equals("STUDENT")
                && !role.equals("OFFICER")) {

            return "redirect:/register?error=invalid-role";
        }

        // Prevent duplicate email registration.
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error=email-exists";
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Store BCrypt hash instead of plaintext password.
        user.setPasswordHash(
                passwordEncoder.encode(password)
        );

        user.setRole(role);

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user != null
                && passwordEncoder.matches(
                        password,
                        user.getPasswordHash())) {

            session.setAttribute(
                    "userId",
                    user.getUserId()
            );

            session.setAttribute(
                    "userName",
                    user.getName()
            );

            session.setAttribute(
                    "userRole",
                    user.getRole()
            );

            if (user.getRole().equals("STUDENT")) {
                return "redirect:/student-dashboard";
            }

            if (user.getRole().equals("OFFICER")) {
                return "redirect:/officer-dashboard";
            }

            if (user.getRole().equals("ADMIN")) {
                return "redirect:/admin-dashboard";
            }
        }

        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login?logout=true";
    }
}