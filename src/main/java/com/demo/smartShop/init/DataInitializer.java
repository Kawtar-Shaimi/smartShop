package com.demo.smartShop.init;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Créer admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        // Créer client
        if (userRepository.findByUsername("client1").isEmpty()) {
            User client = new User();
            client.setUsername("client1");
            client.setPassword("client123");
            client.setRole(UserRole.CLIENT);
            userRepository.save(client);
        }
    }
}