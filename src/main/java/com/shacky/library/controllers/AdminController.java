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

    private final LibraryService service;

    private final AdminRepository adminRepository;

    private final BCryptPasswordEncoder encoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("totalBooks", service.countBooks());
        model.addAttribute("totalUsers", service.countUsers());
        model.addAttribute("borrowedCount", service.countBorrowedBooks());
        model.addAttribute("content", "dashboard");
        return "layout";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("title", "Change Password");
        return "admin/change-password";
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
            return "admin/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "admin/change-password";
        }

        admin.setPassword(encoder.encode(newPassword));
        adminRepository.save(admin);
        model.addAttribute("success", "Password updated successfully.");
        return "admin/change-password";
    }
}
