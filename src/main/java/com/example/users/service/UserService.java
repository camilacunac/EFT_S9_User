package com.example.users.service;

import org.springframework.http.ResponseEntity;

import com.example.users.dto.LoginDto;
import com.example.users.dto.UpdateUserRoleDTO;
import com.example.users.model.UserResponse;
import com.example.users.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    ResponseEntity<UserResponse> getUserById(Long id);

    ResponseEntity<UserResponse> registerUser(User user);

    ResponseEntity<UserResponse> login(LoginDto dataLogin);

    ResponseEntity<UserResponse> updateUserRole(Long idUser, UpdateUserRoleDTO role);

    ResponseEntity<UserResponse> deleteUserById(long idUser);

}
