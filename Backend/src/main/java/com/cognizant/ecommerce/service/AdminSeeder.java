package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        boolean usernameExists = userRepository.findByName("admin").isPresent();
        boolean emailExists = userRepository.findByEmail("admin@yourdomain.com").isPresent();

        if (!usernameExists || !emailExists) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@yourdomain.com");
            admin.setPassword_hash(passwordEncoder.encode("SuperSecurePassword123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Admin user seeded.");
        }
    }
}
