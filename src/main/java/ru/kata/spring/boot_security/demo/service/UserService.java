package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> listUsers();
    void delete(Long id);
    void createUser(String email, byte age, String password, String[] roles, String firstName, String lastName);
    void updateUser(Long id, String email, byte age, String password, String[] roles, String firstName, String lastName);
    User findByEmail(String email);
}
