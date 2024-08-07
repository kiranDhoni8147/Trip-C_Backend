package com.tripc.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripc.userservice.dto.LoginRequestDto;
import com.tripc.userservice.dto.UserDto;
import com.tripc.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setPhoneNumber("1234567890");
        userDto.setPassword("password");

        Mockito.doNothing().when(userService).register(any(UserDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfull,Please Login To Continue"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testRegister_UserAlreadyExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setPhoneNumber("1234567890");
        userDto.setPassword("password");

        Mockito.doThrow(new RuntimeException("User with phone number already exists"))
                .when(userService).register(any(UserDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed. Please check your input and try again."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPhoneNumber("1234567890");
        loginRequestDto.setPassword("password");

        when(userService.login(any(LoginRequestDto.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPhoneNumber("1234567890");
        loginRequestDto.setPassword("password");

        Mockito.doThrow(new RuntimeException("Invalid credentials"))
                .when(userService).login(any(LoginRequestDto.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Login failed. Invalid credentials."))
                .andDo(MockMvcResultHandlers.print());
    }
}
