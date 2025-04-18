package ru.kata.spring.boot_security.demo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;
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

        UserDto adminDto = new UserDto();
        adminDto.setEmail("admin@mail.ru");
        adminDto.setAge((byte) 30);
        adminDto.setPassword("admin");
        adminDto.setRoles(List.of("ROLE_ADMIN", "ROLE_USER"));
        adminDto.setFirstName("AdminFirstName");
        adminDto.setLastName("AdminLastName");

        UserDto userDto = new UserDto();
        userDto.setEmail("user@mail.ru");
        userDto.setAge((byte) 25);
        userDto.setPassword("user");
        userDto.setRoles(List.of("ROLE_USER"));
        userDto.setFirstName("UserFirstName");
        userDto.setLastName("UserLastName");

        userService.createUser(adminDto);

        userService.createUser(userDto);
    }
}
