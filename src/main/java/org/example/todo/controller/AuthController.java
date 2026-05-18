package org.example.todo.controller;

import org.example.todo.dto.AuthRequest;
import org.example.todo.dto.AuthResponse;
import org.example.todo.model.User;
import org.example.todo.repository.UserRepository;
import org.example.todo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AuthController {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtService jwtService;

        private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        @PostMapping("/signup")
        public String signup(@RequestBody User user) {
                if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                        throw new org.example.todo.exception.EmailAlreadyExistsException("Email already exists");
                }

                user.setPassword(
                                encoder.encode(user.getPassword()));

                userRepository.save(user);

                return "User Registered";
        }

        @PostMapping("/login")
        public AuthResponse login(
                        @RequestBody AuthRequest request) {

                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found with this email"));

                if (encoder.matches(
                                request.getPassword(),
                                user.getPassword())) {

                        String token = jwtService.generateToken(user.getEmail());

                        return new AuthResponse(token, user.getName());
                }

                throw new RuntimeException("Invalid Password");
        }
}
