package com.example.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.users.dto.UpdateUserRoleDTO;
import com.example.users.model.User;
import com.example.users.model.UserResponse;
import com.example.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsersTest() {
        // Crear una lista de usuarios simulada
        List<User> userList = new ArrayList<>();
        userList.add(new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville"));
        userList.add(new User("janedoe@example.com", "abc123", "customer", "456 Elm St, Townsville"));
        userList.add(new User("bobsmith@example.com", "qwerty", "customer", "789 Oak St, Villageton"));
        userList.add(new User("admin@example.com", "adminpassword", "admin", "456 Admin Ave, Adminville"));
        userList.add(new User("sarahbrown@example.com", "pass1234", "customer", "202 Cedar St, Groveton"));

        // Simular el comportamiento del UserRepository.findAll() para devolver la lista
        // simulada
        when(userRepository.findAll()).thenReturn(userList);

        // Llamar al método del servicio
        List<User> result = userService.getAllUsers();

        // Verificar que el método devuelve la lista esperada
        assertEquals(userList.size(), result.size());
        assertEquals(userList.get(0).getEmail(), result.get(0).getEmail());
        assertEquals(userList.get(1).getEmail(), result.get(1).getEmail());
        assertEquals(userList.get(2).getEmail(), result.get(2).getEmail());
        assertEquals(userList.get(3).getEmail(), result.get(3).getEmail());
        assertEquals(userList.get(4).getEmail(), result.get(4).getEmail());
    }

    @Test
    void successRegisterUserTest() {
        // Configurar el UserRepository para simular que el email no está registrado
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

        // Crear un nuevo usuario para registrar
        User newUser = new User("newuser@example.com", "NewPassword123@", "customer", "Street 123");

        // Simular el guardado exitoso del usuario
        when(userRepository.save(newUser)).thenReturn(newUser);

        // Llamar al método del servicio
        ResponseEntity<UserResponse> response = userService.registerUser(newUser);

        // Verificar que se devuelve una respuesta exitosa
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getState());
        assertEquals(newUser, response.getBody().getResponse());
        assertTrue(response.getBody().getError().isEmpty());
    }

    @Test
    void getUserByIdTest() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville");
        mockUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<UserResponse> response = userService.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().getState());
        assertEquals(mockUser, response.getBody().getResponse());
    }

    @Test
    void updateUserRoleTest() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville");

        // Act
        UpdateUserRoleDTO roleDTO = new UpdateUserRoleDTO("admin");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        mockUser.setRole("admin");
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        ResponseEntity<UserResponse> response = userService.updateUserRole(userId, roleDTO);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().getState());
        assertEquals("admin", response.getBody().getResponse().getRole());
    }
}
