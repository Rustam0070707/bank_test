package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepo;
import com.example.bankcards.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        registerRequest = new RegisterRequest(
                "testuser",
                "password"

        );

        loginRequest = new LoginRequest("testuser", "password");
    }

    @Test
    void register_WithValidRequest_ShouldReturnSavedUser() {

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);


        User result = userService.register(registerRequest);


        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(passwordEncoder, times(1)).encode(registerRequest.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken("testuser", "USER")).thenReturn("jwt.token.here");


        String token = userService.login(loginRequest);


        assertNotNull(token);
        assertEquals("jwt.token.here", token);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {

        when(userRepository.findAll()).thenReturn(List.of(testUser));


        var result = userService.getAllUsers();


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));


        User result = userService.getUserById(1L);


        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getMyInfo_WithExistingUsername_ShouldReturnUser() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));


        User result = userService.getMyInfo("testuser");


        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void deleteUserById_WithExistingId_ShouldDeleteUser() {

        when(userRepository.existsById(1L)).thenReturn(true);


        userService.deleteUserById(1L);


        verify(userRepository, times(1)).deleteById(1L);
    }
}
