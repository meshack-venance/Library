package com.shacky.library.controllers;

import com.shacky.library.dtos.UserDto;
import com.shacky.library.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String USER_FORM_VIEW = "users/form";
    private static final String REDIRECT_USERS = "redirect:/users";

    private final UserService userService;

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
        addUserFormAttributes(model, new UserDto(), "Add New User");
        return USER_FORM_VIEW;
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
            addUserFormAttributes(model, userDto, "Add New User");
            return USER_FORM_VIEW;
        }

        userService.createUser(userDto);
        return REDIRECT_USERS;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        addUserFormAttributes(model, userService.getUserById(id), "Edit User");
        return USER_FORM_VIEW;
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userDto") UserDto userDto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            addUserFormAttributes(model, userDto, "Edit User");
            return USER_FORM_VIEW;
        }
        userService.updateUser(id, userDto);
        return REDIRECT_USERS;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return REDIRECT_USERS;
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("userDto", userService.getUserById(id));
        model.addAttribute("title", "User Details");
        return "users/view";
    }

    @PostMapping("/import")
    public String importUsersFromExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            userService.importUsersFromExcel(file);
            redirectAttributes.addFlashAttribute("success", "Users imported successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to import users: " + e.getMessage());
        }
        return REDIRECT_USERS;
    }

    private void addUserFormAttributes(Model model, UserDto userDto, String title) {
        model.addAttribute("userDto", userDto);
        model.addAttribute("title", title);
    }

}
