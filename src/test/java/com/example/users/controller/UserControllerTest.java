package com.example.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.users.dto.UpdateUserRoleDTO;
import com.example.users.model.User;
import com.example.users.model.UserResponse;
import com.example.users.service.UserServiceImpl;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userServiceMock;

    @Test
    void getAllUsers() throws Exception {
        // Crear una lista de usuarios simulada
        List<User> userList = new ArrayList<>();
        userList.add(new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville"));
        userList.add(new User("janedoe@example.com", "abc123", "customer", "456 Elm St, Townsville"));
        userList.add(new User("bobsmith@example.com", "qwerty", "customer", "789 Oak St, Villageton"));
        userList.add(new User("admin@example.com", "adminpassword", "admin", "456 Admin Ave, Adminville"));
        userList.add(new User("sarahbrown@example.com", "pass1234", "customer", "202 Cedar St, Groveton"));

        // Simular el comportamiento del servicio para devolver la lista simulada de
        // usuarios
        when(userServiceMock.getAllUsers()).thenReturn(userList);

        // Ejecutar la solicitud GET al endpoint "/users" y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userList.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userList.get(0).getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value(userList.get(0).getRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(userList.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value(userList.get(1).getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].role").value(userList.get(1).getRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(userList.get(2).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].email").value(userList.get(2).getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].role").value(userList.get(2).getRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].id").value(userList.get(3).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].email").value(userList.get(3).getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].role").value(userList.get(3).getRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].id").value(userList.get(4).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].email").value(userList.get(4).getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].role").value(userList.get(4).getRole()));
    }

    @Test
    void registerNewUserTest() throws Exception {
        // Crear un nuevo usuario para registrar
        User newUser = new User("newuser@example.com", "NewPassword123@", "customer", "Street 123");
        UserResponse successResponse = new UserResponse("success", newUser, "");

        // Simular el servicio para devolver la respuesta exitosa al registrar el
        // usuario
        ResponseEntity<UserResponse> responseEntity = ResponseEntity.ok(successResponse);
        when(userServiceMock.registerUser(any(User.class))).thenReturn(responseEntity);

        // Ejecutar la solicitud POST al endpoint "/register" con el nuevo usuario y
        // verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{ \"email\": \"newuser@example.com\", \"password\": \"NewPassword123@\", \"role\": \"customer\", \"direccion\": \"Street 123\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.email").value(newUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value(newUser.getRole()));
    }

    @Test
    void getUserByIdTest() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville");
        UserResponse userResponse = new UserResponse("success", user, "");
        ResponseEntity<UserResponse> serviceResponse = ResponseEntity.ok(userResponse);

        // Act
        when(userServiceMock.getUserById(userId)).thenReturn(serviceResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.email").value(user.getEmail()));
    }

    @Test
    void updateUserRoleTest() throws Exception {
        // Arange
        Long userId = 1L;
        User user = new User("johndoe@example.com", "password123", "customer", "123 Main St, Cityville");
        user.setRole("admin");
        UserResponse userResponse = new UserResponse("success", user, "");
        ResponseEntity<UserResponse> serviceResponse = ResponseEntity.ok(userResponse);

        // Act
        when(userServiceMock.isValidRole("admin")).thenReturn(true);
        when(userServiceMock.updateUserRole(eq(userId), any(UpdateUserRoleDTO.class))).thenReturn(serviceResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{idUser}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"role\": \"admin\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value(user.getRole()));
    }

}
