package com.sprint.training.security.service;

import com.sprint.training.exceptions.ClientAlreadyExistException;
import com.sprint.training.model.Role;
import com.sprint.training.model.User;
import com.sprint.training.security.dto.AuthResponse;
import com.sprint.training.security.dto.LoginRequest;
import com.sprint.training.security.dto.RegisterRequest;
import com.sprint.training.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if(this.userRepository.existsByUsername(request.username())){
            throw new ClientAlreadyExistException("Client with name: " + request.username() + " already exists");
        }

        User user = new User();
        user.setUsername(request.username());

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.valueOf(request.role().toUpperCase()));

        User savedUser = this.userRepository.save(user);

        String token = this.jwtService.generateToken(savedUser);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = this.userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("System user not found: " + request.username()));

        String jwtToken = this.jwtService.generateToken(user);

        return new AuthResponse(jwtToken);
    }
}
