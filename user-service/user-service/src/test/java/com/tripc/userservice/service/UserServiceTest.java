package com.tripc.userservice.service;

import com.tripc.userservice.dto.LoginRequestDto;
import com.tripc.userservice.dto.UserDto;
import com.tripc.userservice.model.User;
import com.tripc.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private LoginRequestDto loginRequestDto;
    private User user;

    @BeforeEach
    public void setup() {
        userDto = new UserDto();
        userDto.setPhoneNumber("1234567890");
        userDto.setPassword("password");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPhoneNumber("1234567890");
        loginRequestDto.setPassword("password");

        user = new User();
        user.setPhoneNumber("1234567890");
        user.setPassword(passwordEncoder.encode("password"));
    }

    @Test
    public void testRegister_Success() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.register(userDto);

        assertNotNull(user);
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());
        assertEquals(user.getPassword(), "encodedPassword");
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(userDto);
        });

        assertEquals("User with phone number already exists", exception.getMessage());
    }

    @Test
    public void testLogin_Success() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String token = userService.login(loginRequestDto);

        assertNotNull(token);
    }

    @Test
    public void testLogin_InvalidCredentials() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequestDto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByPhoneNumber("1234567890");
        });

        assertEquals("User not found with phone number: 1234567890", exception.getMessage());
    }
}
