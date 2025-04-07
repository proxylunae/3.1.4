package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    void add(User user);
    User getUserById(Long id);
    List<User> listUsers();
    void update(User user);
    void delete(Long id);
    void createUser(String username, byte age, String password, String[] roles);
    void updateUser(Long id, String username, byte age, String password);
    User findByUsername(String username);
}
