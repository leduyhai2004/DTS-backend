package com.example.usermanagement.controllers;

import com.example.usermanagement.dto.request.LoginRequest;
import com.example.usermanagement.dto.request.RegisterRequest;
import com.example.usermanagement.dto.response.AuthResponse;
import com.example.usermanagement.entities.Role;
import com.example.usermanagement.entities.User;
import com.example.usermanagement.exception.RoleNotFoundException;
import com.example.usermanagement.repositories.RoleRepository;
import com.example.usermanagement.repositories.UserRepository;
import com.example.usermanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        //check username exists
        if (!userRepository.findByUsername(loginRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("username or password invalid",null));
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse("login successfully",token ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("username or password invalid",null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Username already exists", null));
        }
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email already exists", null));
        }
        // Default role is EMPLOYEE
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse("User registered successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        // Clear the security context
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new AuthResponse("Logged out successfully", null));
    }
}
