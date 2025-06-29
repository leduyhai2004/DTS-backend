package com.example.usermanagement.config;

import com.example.usermanagement.entities.Role;
import com.example.usermanagement.entities.User;
import com.example.usermanagement.enums.UserStatus;
import com.example.usermanagement.repositories.RoleRepository;
import com.example.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final DatabaseInitializer databaseInitializer;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    //hash password
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Starting database and data initialization...");

        // Step 1: Initialize database
        databaseInitializer.initializeDatabase();

        // Step 2: Initialize roles
        initializeRoles();

        // Step 3: Initialize users
        initializeUsers();

        log.info("Database and data initialization completed!");
    }

    private void initializeRoles() {
        log.info("Checking roles data...");

        if (roleRepository.count() == 0) {
            log.info("No roles found. Creating default roles...");

            List<Role> defaultRoles = Arrays.asList(
                Role.builder()
                    .name("ADMIN")
                    .description("Administrator with full system access")
                    .build(),
                Role.builder()
                    .name("USER")
                    .description("Regular user with limited access")
                    .build(),
                Role.builder()
                    .name("MODERATOR")
                    .description("Moderator with content management access")
                    .build(),
                Role.builder()
                    .name("GUEST")
                    .description("Guest user with read-only access")
                    .build()
            );

            roleRepository.saveAll(defaultRoles);
            log.info("Created {} default roles", defaultRoles.size());
        } else {
            log.info("Roles already exist. Count: {}", roleRepository.count());
        }
    }

    private void initializeUsers() {
        log.info("Checking users data...");

        if (userRepository.count() == 0) {
            log.info("No users found. Creating fake users...");

            // Get roles
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            Role userRole = roleRepository.findByName("USER").orElse(null);
            Role moderatorRole = roleRepository.findByName("MODERATOR").orElse(null);
            Role guestRole = roleRepository.findByName("GUEST").orElse(null);

            List<User> fakeUsers = Arrays.asList(
                User.builder()
                    .name("Admin User")
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@example.com")
                    .phone("1234567890")
                    .status(UserStatus.ACTIVE)
                    .role(adminRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("John Doe")
                    .username("johndoe")
                    .password(passwordEncoder.encode("123456"))
                    .email("john.doe@example.com")
                    .phone("1234567891")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Jane Smith")
                    .username("janesmith")
                    .password(passwordEncoder.encode("123456"))
                    .email("jane.smith@example.com")
                    .phone("1234567892")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Mike Johnson")
                    .username("mikejohnson")
                    .password(passwordEncoder.encode("123456"))
                    .email("mike.johnson@example.com")
                    .phone("1234567893")
                    .status(UserStatus.ACTIVE)
                    .role(moderatorRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Sarah Wilson")
                    .username("sarahwilson")
                    .password(passwordEncoder.encode("123456"))
                    .email("sarah.wilson@example.com")
                    .phone("1234567894")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Bob Brown")
                    .username("bobbrown")
                    .password(passwordEncoder.encode("123456"))
                    .email("bob.brown@example.com")
                    .phone("1234567895")
                    .status(UserStatus.INACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Guest User")
                    .username("guest")
                    .password(passwordEncoder.encode("123456"))
                    .email("guest@example.com")
                    .phone("1234567896")
                    .status(UserStatus.ACTIVE)
                    .role(guestRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Alice Cooper")
                    .username("alicecooper")
                    .password(passwordEncoder.encode("123456"))
                    .email("alice.cooper@example.com")
                    .phone("1234567897")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Tom Davis")
                    .username("tomdavis")
                    .password(passwordEncoder.encode("123456"))
                    .email("tom.davis@example.com")
                    .phone("1234567898")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build(),

                User.builder()
                    .name("Lisa Garcia")
                    .username("lisagarcia")
                    .password(passwordEncoder.encode("123456"))
                    .email("lisa.garcia@example.com")
                    .phone("1234567899")
                    .status(UserStatus.ACTIVE)
                    .role(userRole)
                    .isDeleted(false)
                    .build()
            );

            userRepository.saveAll(fakeUsers);
            log.info("Created {} fake users", fakeUsers.size());

            // Log created users for reference
            log.info("Created users:");
            fakeUsers.forEach(user ->
                log.info("- {} ({}): {} - Role: {}",
                    user.getName(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole() != null ? user.getRole().getName() : "NO_ROLE")
            );

        } else {
            log.info("Users already exist. Count: {}", userRepository.count());
        }
    }
}
