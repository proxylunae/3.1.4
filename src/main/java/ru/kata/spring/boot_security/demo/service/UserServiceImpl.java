package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserDto;
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
    public void createUser(UserDto dto) {
        Set<Role> roles = dto.getRoles().stream()
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (dto.getRoles().contains("ROLE_ADMIN")) {
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) {
                roles.add(userRole);
            }
        }

        User user = new User(
                dto.getEmail(),
                dto.getAge(),
                passwordEncoder.encode(dto.getPassword()),
                roles,
                dto.getFirstName(),
                dto.getLastName()
        );
        userRepository.add(user);
    }

    @Override
    public void updateUser(Long id, UserDto dto) {
        User user = userRepository.getUserById(id);
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        updatePasswordIfNeeded(user, dto.getPassword());

        Set<Role> roles = dto.getRoles().stream()
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (dto.getRoles().contains("ROLE_ADMIN")) {
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
