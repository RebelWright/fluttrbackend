package com.revature.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.LoginRequest;
import com.revature.dtos.RegisterRequest;
import com.revature.models.User;
import com.revature.services.AuthService;
import com.revature.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)


public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
    }

    @Test
    public void loginSuccess() throws Exception {
        User testUser2 = new User(2, "blorp@email.com", "blorp", "Bob", "Smith", "BSmi", null, null, "image2.com");

        LoginRequest testLoginR = new LoginRequest();
        testLoginR.setEmail("blorp@email.com");
        testLoginR.setPassword("blorp");
        String requestBody = objectMapper.writeValueAsString(testLoginR);

        given(authService.findByCredentials(testLoginR.getEmail(),testLoginR.getPassword())).willReturn(Optional.of(testUser2));
        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.email", is(testUser2.getEmail())))
                .andExpect(jsonPath("$.password", is(testUser2.getPassword())))
                .andExpect(jsonPath("$.firstName", is(testUser2.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser2.getLastName())))
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())));
    }

    @Test
    public void loginFail() throws Exception {
        User testUser2 = new User(2, "blorp@email.com", "blorp", "Bob", "Smith", "BSmi", null, null, "image2.com");

        LoginRequest testLoginR = new LoginRequest();
        testLoginR.setEmail("blorp@email.com");
        testLoginR.setPassword("blorp");
        String requestBody = objectMapper.writeValueAsString(testLoginR);

        given(authService.findByCredentials(testLoginR.getEmail(),testLoginR.getPassword())).willReturn(Optional.of(testUser2));
        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.email", is(testUser2.getEmail())))
                .andExpect(jsonPath("$.password", is(testUser2.getPassword())))
                .andExpect(jsonPath("$.firstName", is(testUser2.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser2.getLastName())))
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())));
    }

    @Test
    public void registerSuccessful() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        testUser1.setId(1);
        RegisterRequest testReg = new RegisterRequest();
        testReg.setEmail("test.com");
        testReg.setUsername("JDoe");
        given(authService.register(testUser1)).willReturn(testUser1);

        String requestBody = objectMapper.writeValueAsString(testUser1);
        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(201));
                /*.andExpect(jsonPath("$.email", is(testUser1.getEmail())))
                .andExpect(jsonPath("$.password", is(testUser1.getPassword())))
                .andExpect(jsonPath("$.firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("$.username", is(testUser1.getUsername())))*/
       /* .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email[0]", is("test.com")))
                .andExpect(jsonPath("$.password", is(testUser1.getPassword())))
                .andExpect(jsonPath("$.firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("$.username", is(testUser1.getUsername())))
                .andExpect(jsonPath("$.imageUrl", is(testUser1.getImageUrl())))*/
    }


    @Test
    public void registerFail() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        testUser1.setId(1);
        RegisterRequest testReg = new RegisterRequest();
        testReg.setEmail("test.com");
        testReg.setUsername("JDoe");
        given(authService.register(testUser1)).willReturn(null);
        //given(authService.register(testUser1)).willReturn(testUser1);
        String requestBody = objectMapper.writeValueAsString(testUser1);
        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((byte[]) null))
                .andExpect(status().is(400));
    }

}