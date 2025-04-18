package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> listUsers();
    void delete(Long id);
    void createUser(UserDto userDto);
    void updateUser(Long id, UserDto userDto);
    User findByEmail(String email);
}
