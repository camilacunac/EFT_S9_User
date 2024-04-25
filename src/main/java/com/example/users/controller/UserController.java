package com.example.users.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.users.dto.LoginDto;
import com.example.users.dto.UpdateUserRoleDTO;
import com.example.users.model.UserResponse;
import com.example.users.model.User;
import com.example.users.service.UserService;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<EntityModel<UserResponse>> registerUser(@RequestBody User user) {
        ResponseEntity<UserResponse> responseEntity = userService.registerUser(user);
        EntityModel<UserResponse> responseResource = EntityModel.of(responseEntity.getBody());
        if (responseEntity.getBody().getError().equals("")) {
            responseResource.add(
                    WebMvcLinkBuilder.linkTo(UserController.class).slash("user").slash(user.getId()).withSelfRel());
        }
        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(responseResource);
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<EntityModel<UserResponse>> loginUser(@RequestBody LoginDto dataLogin) {
        UserResponse loginResponse = userService.login(dataLogin).getBody();

        // Crear enlaces HATEOAS para recursos relacionados
        EntityModel<UserResponse> responseResource = EntityModel.of(loginResponse);
        if (loginResponse != null && loginResponse.getResponse().getId() != null) {
            responseResource.add(WebMvcLinkBuilder.linkTo(UserController.class).slash("user")
                    .slash(loginResponse.getResponse().getId()).withSelfRel());
        }
        // Agregar más enlaces según sea necesario

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseResource);
    }

    @PutMapping(value = "/user/{idUser}", produces = "application/json")
    public ResponseEntity<EntityModel<UserResponse>> updateUserRole(@PathVariable Long idUser,
            @RequestBody UpdateUserRoleDTO role) {
        ResponseEntity<UserResponse> responseEntity = userService.updateUserRole(idUser, role);

        EntityModel<UserResponse> responseResource = EntityModel.of(responseEntity.getBody());
        responseResource.add(WebMvcLinkBuilder.linkTo(UserController.class).slash("users").withRel("all-users"));
        if (responseEntity.getBody().getError().equals("")) {
            responseResource
                    .add(WebMvcLinkBuilder.linkTo(UserController.class).slash("user").slash(idUser).withSelfRel());
        }
        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(responseResource);
    }

    @DeleteMapping(value = "/user/delete/{idUser}", produces = "application/json")
    public ResponseEntity<EntityModel<UserResponse>> deleteUser(@PathVariable Long idUser) {
        ResponseEntity<UserResponse> responseEntity = userService.deleteUserById(idUser);

        EntityModel<UserResponse> responseResource = EntityModel.of(responseEntity.getBody());
        responseResource.add(WebMvcLinkBuilder.linkTo(UserController.class).slash("/users").withRel("all-users"));

        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(responseResource);
    }

    @GetMapping(value = "/users")
    public List<EntityModel<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .map((User user) -> EntityModel.of(user,
                        WebMvcLinkBuilder.linkTo(UserController.class).slash("/user").slash(user.getId())
                                .withSelfRel()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(@PathVariable Long id) {
        ResponseEntity<UserResponse> responseEntity = userService.getUserById(id);

        // Crear enlaces HATEOAS para recursos relacionados
        EntityModel<UserResponse> responseResource = EntityModel.of(responseEntity.getBody());
        responseResource.add(WebMvcLinkBuilder.linkTo(UserController.class).slash("users").withRel("all-users"));
        if (responseEntity.getBody().getError().equals("")) {
            responseResource.add(WebMvcLinkBuilder.linkTo(UserController.class).slash("user").slash(id).withSelfRel());
        }
        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(responseResource);
    }
}
