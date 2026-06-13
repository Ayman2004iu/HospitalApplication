package com.aymanibrahim.hospital.service.impl;

import com.aymanibrahim.hospital.dto.request.LoginRequest;
import com.aymanibrahim.hospital.dto.request.RegisterRequest;
import com.aymanibrahim.hospital.dto.response.AuthResponses;
import com.aymanibrahim.hospital.entity.Role;
import com.aymanibrahim.hospital.entity.User;
import com.aymanibrahim.hospital.enums.RoleName;
import com.aymanibrahim.hospital.exception.BusinessLogicException;
import com.aymanibrahim.hospital.exception.ResourceNotFoundException;
import com.aymanibrahim.hospital.repository.RoleRepository;
import com.aymanibrahim.hospital.repository.UserRepository;
import com.aymanibrahim.hospital.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldCreateUser_WhenValidRequest() {
        RegisterRequest request = new RegisterRequest("ayman", "ayman@example.com", "password123", "Ayman Ibrahim");

        Role role = new Role();
        role.setName(RoleName.ROLE_PATIENT);

        when(userRepository.existsByUsername("ayman")).thenReturn(false);
        when(userRepository.existsByEmail("ayman@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_PATIENT)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("ayman@example.com")).thenReturn("jwt-token");

        AuthResponses response = authService.register(request);

        verify(userRepository).save(any(User.class));
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("ayman", response.getUsername());
        assertEquals("ayman@example.com", response.getEmail());
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("ayman", "ayman@example.com", "password123", "Ayman Ibrahim");
        when(userRepository.existsByUsername("ayman")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest("ayman", "ayman@example.com", "password123", "Ayman Ibrahim");
        when(userRepository.existsByUsername("ayman")).thenReturn(false);
        when(userRepository.existsByEmail("ayman@example.com")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowException_WhenRoleNotFound() {
        RegisterRequest request = new RegisterRequest("ayman", "ayman@example.com", "password123", "Ayman Ibrahim");
        when(userRepository.existsByUsername("ayman")).thenReturn(false);
        when(userRepository.existsByEmail("ayman@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_PATIENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        LoginRequest request = new LoginRequest("ayman@example.com", "password123");

        User user = new User();
        user.setEmail("ayman@example.com");
        user.setUsername("ayman");

        when(userRepository.findByEmail("ayman@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("ayman@example.com")).thenReturn("jwt-token");

        AuthResponses response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("ayman@example.com", "password123")
        );

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("ayman", response.getUsername());
        assertEquals("ayman@example.com", response.getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password123");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }
}