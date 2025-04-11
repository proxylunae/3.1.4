package ru.kata.spring.boot_security.demo.repository;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserRepository {
    void add(User user);
    User getUserById(Long id);
    List<User> listUsers();
    void update(User user);
    void delete(Long id);
    User findByUsername(String username);
}
