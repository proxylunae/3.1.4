package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    public void add(User user) {
        userRepository.add(user);
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
    public void update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.update(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public void createUser(String username, byte age, String password, String[] roleNames) {
        Set<Role> roles = Arrays.stream(roleNames)
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());

        boolean hasAdmin = Arrays.asList(roleNames).contains("ROLE_ADMIN");
        if (hasAdmin) {
            roles.add(roleRepository.findByName("ROLE_USER"));
        }

        User user = new User(username, age, passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.add(user);
    }

    @Override
    public void updateUser(Long id, String username, byte age, String password, String[] roleNames) {
        User user = userRepository.getUserById(id);
        user.setUsername(username);
        user.setAge(age);

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        Set<Role> roles = Arrays.stream(roleNames)
                .map(Long::parseLong)
                .map(roleRepository::findById)
                .map(opt -> opt.orElse(null))
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


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
