package com.shacky.library.controllers;

import com.shacky.library.entities.Admin;
import com.shacky.library.repositories.AdminRepository;
import com.shacky.library.services.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String CHANGE_PASSWORD_VIEW = "admin/change-password";

    private final LibraryService libraryService;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("totalBooks", libraryService.countBooks());
        model.addAttribute("totalUsers", libraryService.countUsers());
        model.addAttribute("borrowedCount", libraryService.countBorrowedBooks());
        model.addAttribute("content", "dashboard");
        return "layout";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("title", "Change Password");
        return CHANGE_PASSWORD_VIEW;
    }

    @PostMapping("/change-password")
    public String handlePasswordChange(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Admin admin = adminRepository.findByUsername(username)
                .orElse(null);

        if (admin == null || !encoder.matches(currentPassword, admin.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return CHANGE_PASSWORD_VIEW;
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return CHANGE_PASSWORD_VIEW;
        }

        admin.setPassword(encoder.encode(newPassword));
        adminRepository.save(admin);
        model.addAttribute("success", "Password updated successfully.");
        return CHANGE_PASSWORD_VIEW;
    }
}
