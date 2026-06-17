package com.shacky.library.common.web;

import com.shacky.library.common.exception.BusinessRuleException;
import com.shacky.library.common.exception.DuplicateResourceException;
import com.shacky.library.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        addErrorAttributes(model, HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return "error/custom-error";
    }

    @ExceptionHandler({BusinessRuleException.class, DuplicateResourceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessError(RuntimeException ex, HttpServletRequest request, Model model) {
        addErrorAttributes(model, HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        return "error/custom-error";
    }

    private void addErrorAttributes(Model model, HttpStatus status, String message, String path) {
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", message);
        model.addAttribute("path", path);
    }
}
