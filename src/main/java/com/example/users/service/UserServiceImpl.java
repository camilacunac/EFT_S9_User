package com.example.users.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.users.dto.LoginDto;
import com.example.users.dto.UpdateUserRoleDTO;
import com.example.users.model.UserResponse;
import com.example.users.model.User;
import com.example.users.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(Long id) {
        UserResponse res;
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                res = new UserResponse("success", user.get(), "");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }
            res = new UserResponse("error", new User(), "No se encontro un usuario con ese id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (Exception e) {
            res = new UserResponse("error", new User(), "Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<UserResponse> registerUser(User user) {
        UserResponse res;
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }

            if (!isValidEmail(user.getEmail())) {
                throw new IllegalArgumentException("El email ingresado no es valido");
            }

            if (!isValidPassword(user.getPassword())) {
                throw new IllegalArgumentException(
                        "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula y 1 caracter especial");
            }

            if (!isValidRole(user.getRole())) {
                throw new IllegalArgumentException(
                        "El rol ingresado no es valido");
            }

            String contraseñaEncriptada = encryptPassword(user.getPassword());
            user.setPassword(contraseñaEncriptada);
            User savedUser = userRepository.save(user);
            res = new UserResponse("success", savedUser, "");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (IllegalArgumentException e) {
            res = new UserResponse("error", new User(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (Exception e) {
            res = new UserResponse("error", new User(), "Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<UserResponse> login(LoginDto dataLogin) {
        UserResponse res;
        User user = userRepository.findByEmail(dataLogin.getEmail());
        if (user != null) {
            String encriptedPass = user.getPassword();
            if (passwordEncoder.matches(dataLogin.getPassword(), encriptedPass)) {
                res = new UserResponse("success", user, "");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            } else {
                res = new UserResponse("error", new User(), "Bad Request: Email o contraseña incorrecto");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
        }
        res = new UserResponse("error", new User(), "Email no existe");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserRole(Long idUser, UpdateUserRoleDTO role) {
        UserResponse res;
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                if (!isValidRole(role.getRole())) {
                    res = new UserResponse("error", new User(), "El role ingresado no es valido");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                }
                User updatedUser = user.get();
                updatedUser.setRole(role.getRole());
                User savedUser = userRepository.save(updatedUser);
                res = new UserResponse("success", savedUser, "");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }
            res = new UserResponse("error", new User(), "No se encontro un usuario con ese id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (Exception e) {
            res = new UserResponse("error", new User(), "Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Override
    public ResponseEntity<UserResponse> deleteUserById(long idUser) {
        UserResponse res;
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                userRepository.deleteById(idUser);
                res = new UserResponse("success",
                        user.get(),
                        "");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }
            res = new UserResponse("error", new User(), "No se encontro ningun usuario con ese id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (Exception e) {
            res = new UserResponse("error", new User(), "Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    private String encryptPassword(String contraseña) {
        return passwordEncoder.encode(contraseña);
    }

    public boolean isValidRole(String role) {
        String lowerCaseRole = role.toLowerCase();
        return lowerCaseRole.equals("admin") || lowerCaseRole.equals("customer");
    }

    public boolean isValidEmail(String email) {
        // Patrón regex para validar la estructura básica del correo electrónico
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}
