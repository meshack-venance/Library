package com.shacky.library.controllers;

import com.shacky.library.dtos.UserDto;
import com.shacky.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // List all users
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> userPage = userService.getAllUsers(pageable);

        model.addAttribute("title", "Users");
        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("serialStart", page * size);

        return "users/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("title", "Add New User");
        return "users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userDto") UserDto userDto,
                             BindingResult result, Model model) {

        boolean exists = userService.existsByFullName(
                userDto.getFirstName(),
                userDto.getMiddleName(),
                userDto.getLastName()
        );

        if (exists) {
            result.reject("user.exists", "User with the same full name already exists.");
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Add New User");
            return "users/form";
        }

        userService.createUser(userDto);
        return "redirect:/users";
    }




    // Show form to edit existing user
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserDto userDto = userService.getUserById(id);
        model.addAttribute("userDto", userDto);
        model.addAttribute("title", "Edit User");
        return "users/form";
    }

    // Handle POST to update existing user
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userDto") UserDto userDto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Edit User");
            return "users/form";
        }
        userService.updateUser(id, userDto);
        return "redirect:/users";
    }

    // Delete user by id
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    // Optional: View user details
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        UserDto userDto = userService.getUserById(id);
        model.addAttribute("userDto", userDto);
        model.addAttribute("title", "User Details");
        return "users/view";  // Thymeleaf template: users/view.html
    }

    @PostMapping("/import")
    public String importUsersFromExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            userService.importUsersFromExcel(file);
            model.addAttribute("success", "Users imported successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to import users: " + e.getMessage());
        }
        return "redirect:/users";
    }

}
