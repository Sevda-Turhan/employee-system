package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DataLoaderConfig {
    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("sevda");
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setRole("ADMIN");  // burası String olmalı
                userRepository.save(admin);
            }
                // admin kullanıcısı
                if (userRepository.findByUsername("personel") == null) {
                    User user = new User();
                    user.setUsername("personel");
                    user.setPassword(passwordEncoder.encode("1234"));
                    user.setRole("USER");
                    userRepository.save(user);

                }



        };


    }
}
