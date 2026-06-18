package com.shacky.library.controllers;

import com.shacky.library.common.exception.ResourceNotFoundException;
import com.shacky.library.dtos.BookDto;
import com.shacky.library.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private static final String BOOK_FORM_VIEW = "books/form";
    private static final String REDIRECT_BOOKS = "redirect:/books";

    private final BookService bookService;

    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookDto> bookPage = bookService.getAllBooks(pageable);

        model.addAttribute("title", "Book List");
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", bookPage.getTotalPages());

        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        addBookFormAttributes(model, new BookDto(), "Add New Book");
        return BOOK_FORM_VIEW;
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") BookDto bookDto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            addBookFormAttributes(model, bookDto, "Book Form");
            return BOOK_FORM_VIEW;
        }

        bookService.saveBook(bookDto);
        return REDIRECT_BOOKS;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        addBookFormAttributes(model, findBook(id), "Edit Book");
        return BOOK_FORM_VIEW;
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return REDIRECT_BOOKS;
    }

    @GetMapping("/{id}")
    public String viewBookDetails(@PathVariable Long id, Model model) {
        model.addAttribute("book", findBook(id));
        return "books/details";
    }

    @PostMapping("/import")
    public String importBooksFromExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            bookService.importBooks(file);
            redirectAttributes.addFlashAttribute("success", "Books imported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to import: " + e.getMessage());
        }
        return REDIRECT_BOOKS;
    }

    private BookDto findBook(Long id) {
        return bookService.getBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }

    private void addBookFormAttributes(Model model, BookDto book, String title) {
        model.addAttribute("book", book);
        model.addAttribute("title", title);
    }

}
