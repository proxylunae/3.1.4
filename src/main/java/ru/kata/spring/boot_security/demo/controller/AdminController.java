package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")   // Добавлено
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "user-list";
    }

    @GetMapping("/add")
    public String showAddUserForm(@RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "username", required = false) String username,
                                  @RequestParam(value = "age", required = false) Byte age,
                                  Model model) {
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        model.addAttribute("age", age);
        return "user-add";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("age") byte age,
                          @RequestParam("password") String password,
                          @RequestParam(value = "roles", required = false) String[] roles) {

        if (roles == null || roles.length == 0) {
            return "redirect:/admin/add?error=roles_required"
                    + "&username=" + username
                    + "&age=" + age;
        }

        userService.createUser(username, age, password, roles);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam("id") Long id,
                           @RequestParam("username") String username,
                           @RequestParam("age") byte age,
                           @RequestParam(value = "password", required = false) String password) {
        userService.updateUser(id, username, age, password);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Test error");
    }

    @GetMapping("/fail")
    public String fail() {
        int x = 1 / 0;
        return "index";
    }
}
