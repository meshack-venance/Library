package com.shacky.library.config;

import com.shacky.library.entities.Admin;
import com.shacky.library.repositories.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Bean
    CommandLineRunner initAdmin(AdminRepository adminRepo,
                                BCryptPasswordEncoder encoder,
                                AdminBootstrapProperties adminProperties) {
        return args -> {
            if (adminRepo.findAll().isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername(adminProperties.getUsername());
                admin.setPassword(encoder.encode(adminProperties.getPassword()));
                adminRepo.save(admin);
                log.info("Default admin account created for username '{}'", adminProperties.getUsername());
            }
        };
    }
}
