package com.springboot.authready.utils;

import com.springboot.authready.entity.Role;
import com.springboot.authready.entity.User;
import com.springboot.authready.repository.RoleRepository;
import com.springboot.authready.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;

@Component @RequiredArgsConstructor @Transactional
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeUser();
    }

    public void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role USER = new Role();
            USER.setName("USER");
            Role ADMIN = new Role();
            ADMIN.setName("ADMIN");
            Role ROOT = new Role();
            ROOT.setName("ROOT");
            roleRepository.saveAll(Arrays.asList(USER, ADMIN, ROOT));
        }
    }

    private void initializeUser() {
        if (userRepository.count() == 0) {
            User root = new User();
            root.setUsername("superuser");
            root.setEmail("superuser@java9airlines.com");
            root.setPassword(passwordEncoder.encode("morethanroot"));
            root.setIsEnabled(true);
            root.getRoles().add(roleRepository.findByName("ROOT").orElse(null));
            root.getRoles().add(roleRepository.findByName("ADMIN").orElse(null));
            userRepository.save(root);
        }
    }
}
