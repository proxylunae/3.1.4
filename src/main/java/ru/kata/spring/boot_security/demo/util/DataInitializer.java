package ru.kata.spring.boot_security.demo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");

        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        User admin = new User("admin", (byte) 30, passwordEncoder.encode("admin"), Set.of(adminRole, userRole));
        User user = new User("user", (byte) 25, passwordEncoder.encode("user"), Set.of(userRole));

        userService.add(admin);
        userService.add(user);

        System.out.println("DataInitializer encoder: " + passwordEncoder.getClass());
        System.out.println("Хэш admin: " + passwordEncoder.encode("admin"));
        System.out.println("Сравнение: " + passwordEncoder.matches("admin", passwordEncoder.encode("admin")));
    }
}
