package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.listUsers();
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public void createUser(String email, byte age, String password, String[] roleNames, String firstName, String lastName) {
        Set<Role> roles = Arrays.stream(roleNames)
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        boolean hasAdmin = Arrays.asList(roleNames).contains("ROLE_ADMIN");
        if (hasAdmin) {
            roles.add(roleRepository.findByName("ROLE_USER"));
        }

        User user = new User(email, age, passwordEncoder.encode(password), roles, firstName, lastName);
        user.setRoles(roles);
        userRepository.add(user);
    }

    @Override
    public void updateUser(Long id, String email, byte age, String password, String[] roleNames, String firstName, String lastName) {
        User user = userRepository.getUserById(id);
        user.setEmail(email);
        user.setAge(age);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        updatePasswordIfNeeded(user, password);

        Set<Role> roles = Arrays.stream(roleNames)
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        boolean hasAdmin = roles.stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        if (hasAdmin) {
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) {
                roles.add(userRole);
            }
        }

        user.setRoles(roles);
        userRepository.update(user);
    }

    private void updatePasswordIfNeeded(User user, String password) {
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
    }



    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
