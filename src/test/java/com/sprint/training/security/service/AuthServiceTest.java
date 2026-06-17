package com.sprint.training.security.service;

import com.sprint.training.security.model.Role;
import com.sprint.training.security.model.User;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
        LoginRequest request = new LoginRequest("danek", "admin");

        User fakeUser = new User();
        fakeUser.setUsername("danek");
        fakeUser.setRole(Role.ADMIN);

        String expectedToken = "mocked-jwt-token";


        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(fakeUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtService.generateToken(fakeUser)).thenReturn(expectedToken);

        AuthResponse response = authService.login(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedToken, response.accessToken());
    }

    @Test
    public void login_whenUserDoesNotExist_shouldThrowBadCredentialsException() {

        LoginRequest request = new LoginRequest("unknown", "password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));


        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));
    }

}
