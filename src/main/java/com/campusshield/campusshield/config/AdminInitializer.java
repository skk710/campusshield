package com.campusshield.campusshield.config;

import com.campusshield.campusshield.entity.User;
import com.campusshield.campusshield.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {

        return args -> {

            BCryptPasswordEncoder passwordEncoder =
                    new BCryptPasswordEncoder();

            String email = "campusadmin@gmail.com";
            String password = "password";

            User admin =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);

            if (admin == null) {

                admin = new User();

                admin.setName("Campus Admin");
                admin.setEmail(email);
                admin.setRole("ADMIN");

                admin.setPasswordHash(
                        passwordEncoder.encode(password)
                );

                userRepository.save(admin);

                System.out.println(
                        "======================================"
                );
                System.out.println(
                        "CampusShield ADMIN account created"
                );
                System.out.println(
                        "Email: " + email
                );
                System.out.println(
                        "Password: " + password
                );
                System.out.println(
                        "======================================"
                );

            } else {

                admin.setName("Campus Admin");
                admin.setRole("ADMIN");

                admin.setPasswordHash(
                        passwordEncoder.encode(password)
                );

                userRepository.save(admin);

                System.out.println(
                        "======================================"
                );
                System.out.println(
                        "CampusShield ADMIN account updated"
                );
                System.out.println(
                        "Email: " + email
                );
                System.out.println(
                        "Password: " + password
                );
                System.out.println(
                        "======================================"
                );
            }
        };
    }
}