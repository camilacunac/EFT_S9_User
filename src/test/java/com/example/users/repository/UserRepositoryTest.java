package com.example.users.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.expression.spel.ast.OpDec;

import com.example.users.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsersTest() {
        // Llamar al método findAll() del repositorio
        List<User> userList = userRepository.findAll();

        // Verificar que la lista de usuarios no esté vacía
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
    }

    @Test
    void saveUserTest() {

        // Crea usuario
        User user = new User("test@gmail.com", "tesT123@", "customer", "Calle 123");

        // Guarda usuario en BD
        User savedUser = userRepository.save(user);

        // Verifica que el id existe y que el email coincida
        assertNotNull(savedUser.getId());
        assertEquals("test@gmail.com", savedUser.getEmail());
    }

}
