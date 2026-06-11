package com.sprint.training.controller;


import com.sprint.training.security.config.SecurityConfig;
import com.sprint.training.security.filter.JwtAuthenticationFilter;
import com.sprint.training.service.ClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(SecurityConfig.class)
public class ClientControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @TestConfiguration
    static class TestFilterConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(null, null) {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    @Test
    public void createClient_whenUserIsGuard_shouldReturn403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/clients")
                        .with(user("guardUser").roles("GUARD"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Client",
                                  "email": "test@mail.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createClient_whenUserIsAdmin_shouldNotReturn403Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/clients")
                        .with(user("adminUser").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Valid Client",
                                  "email": "valid@mail.com"
                                }
                                """))
                .andExpect(status().is2xxSuccessful()); // Доступ будет успешно открыт, так как роль ADMIN не сотрется!
    }
}
