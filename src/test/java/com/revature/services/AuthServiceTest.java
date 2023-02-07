package com.revature.services;

import com.revature.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private AuthService authService;


    @Test
    void findByCredentialsSuccess() {
        User mockUser = new User("blorp@email.com", "blorp", "david", "mata", "norping");

        when(authService.findByCredentials("blorp@email.com", "blorp")).thenReturn(Optional.of(mockUser));

        Optional<User> resultUser = authService.findByCredentials("blorp@email.com", "blorp");

        assertEquals(resultUser.get().getEmail(), mockUser.getEmail());
        assertEquals(resultUser.get().getPassword(), mockUser.getPassword());
    }

    @Test
    void findByCredentialsFail() {
        User mockUser = new User ("blorp@email.com","blorp","david","mata", "norping");

        when(authService.findByCredentials("blorp@email.com", "blorp")).thenReturn(Optional.of(mockUser));

        Optional<User> resultUser = authService.findByCredentials("blorp@email.com", "blorp");

        assertNotEquals("blorpnorp",mockUser.getEmail());
        assertNotEquals("chicaca",mockUser.getPassword());

        assertNotEquals(resultUser.get().getEmail(),"blorpnorp");
        assertNotEquals(resultUser.get().getPassword(),"chicaca");

    }

    @Test
    void registerSuccess() {
        User mockUser = new User ("blorp@email.com","blorp","david","mata", "norping");

        when(authService.register(mockUser)).thenReturn(mockUser);

        User rUser = authService.register(mockUser);

        verify(userService).save(mockUser);
        assertEquals(mockUser,rUser);
    }

    @Test
    void registerFail() {
        User mockUser = new User ("blorp@email.com","blorp","david","mata", "norping");

        when(authService.register(mockUser)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> authService.register(mockUser));
        verify(userService).save(mockUser);
    }
}