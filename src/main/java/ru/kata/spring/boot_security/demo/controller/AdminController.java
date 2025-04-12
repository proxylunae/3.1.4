package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin")   // Добавлено
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("currentUser", userService.findByEmail(principal.getName()));
        return "admin";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam("email") String email,
                          @RequestParam("age") byte age,
                          @RequestParam("password") String password,
                          @RequestParam(value = "roles", required = false) String[] roles,
                          @RequestParam(value = "firstName") String firstName,
                          @RequestParam(value = "lastName") String lastName) {

        if (roles == null || roles.length == 0) {
            return "redirect:/admin/add?error=roles_required"
                    + "&email=" + email
                    + "&age=" + age
                    + "&firstName=" + firstName
                    + "&lastName=" + lastName;
        }

        userService.createUser(email, age, password, roles, firstName, lastName);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam("id") Long id,
                           @RequestParam("email") String email,
                           @RequestParam("age") byte age,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam("roles") String[] roleNames,
                           @RequestParam(value = "firstName") String firstName,
                           @RequestParam(value = "lastName") String lastName) {
        userService.updateUser(id, email, age, password, roleNames, firstName, lastName);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
