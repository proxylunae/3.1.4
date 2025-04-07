package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void add(User user) {
        userDao.add(user);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    public void update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.update(user);
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
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
        userDao.add(user);
    }

    @Override
    public void updateUser(Long id, String username, byte age, String password) {
        User user = userDao.getUserById(id);
        user.setUsername(username);
        user.setAge(age);

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userDao.update(user);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
