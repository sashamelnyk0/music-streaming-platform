package com.music.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.user.dto.AuthResponseDto;
import com.music.user.dto.LoginRequestDto;
import com.music.user.dto.RegisterRequestDto;
import com.music.user.exception.EmailAlreadyExistsException;
import com.music.user.exception.GlobalExceptionHandler;
import com.music.user.exception.InvalidCredentialsException;
import com.music.user.security.JwtAuthFilter;
import com.music.user.security.JwtService;
import com.music.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({ValidationAutoConfiguration.class,
        GlobalExceptionHandler.class,
        AuthControllerTest.SecurityTestConfig.class})
class AuthControllerTest {

    @TestConfiguration
    @EnableWebMvc
    static class SecurityTestConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private AuthResponseDto authResponse;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));

        registerRequest = new RegisterRequestDto();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("password123");

        authResponse = AuthResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
    }

    @Test
    @DisplayName("successful registration returns 201 with tokens")
    void register_success_returns201() throws Exception {
        when(userService.register(any(RegisterRequestDto.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("registration with existing email returns 409")
    void register_emailExists_returns409() throws Exception {
        when(userService.register(any(RegisterRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("test@gmail.com"));

        mockMvc.perform(post("/api/v1/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("registration with blank name returns 400")
    void register_blankName_returns400() throws Exception {
        registerRequest.setName("");

        mockMvc.perform(post("/api/v1/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registration with invalid email returns 400")
    void register_invalidEmail_returns400() throws Exception {
        registerRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registration with short password returns 400")
    void register_shortPassword_returns400() throws Exception {
        registerRequest.setPassword("123");

        mockMvc.perform(post("/api/v1/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("successful login returns 200 with tokens")
    void login_success_returns200() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("login with wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenThrow(new InvalidCredentialsException("Password doesn't match"));

        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login with blank email returns 400")
    void login_blankEmail_returns400() throws Exception {
        loginRequest.setEmail("");

        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login with blank password returns 400")
    void login_blankPassword_returns400() throws Exception {
        loginRequest.setPassword("");

        mockMvc.perform(post("/api/v1/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}