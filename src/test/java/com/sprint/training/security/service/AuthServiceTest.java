package com.sprint.training.security.service;

import com.sprint.training.model.Role;
import com.sprint.training.model.User;
import com.sprint.training.security.dto.AuthResponse;
import com.sprint.training.security.dto.LoginRequest;
import com.sprint.training.security.repository.RefreshTokenRepository;
import com.sprint.training.security.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_whenCredentialsAreValid_shouldReturnAuthResponseWithToken() {
        //ARRANGE
        LoginRequest request = new LoginRequest("danek", "admin");

        User fakeUser = new User();
        fakeUser.setUsername("danek");
        fakeUser.setRole(Role.ADMIN);

        String expectedToken = "mocked-jwt-token";

        when(userRepository.findByUsername("danek")).thenReturn(Optional.of(fakeUser));

        when(jwtService.generateToken(fakeUser)).thenReturn(expectedToken);

        //ACT
        AuthResponse response = authService.login(request);

        // ASSERT
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedToken, response.accessToken());
    }

    @Test
    public void login_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {

        LoginRequest request = new LoginRequest("unknown", "password");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());


        UsernameNotFoundException exception = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(request)
        );

        Assertions.assertEquals("System user not found: unknown", exception.getMessage());
    }

}
