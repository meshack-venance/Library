package com.shacky.library.controllers;

import com.shacky.library.dtos.TransactionDto;
import com.shacky.library.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // List all transactions + Search by user
    @GetMapping
    public String listTransactions(@RequestParam(required = false) String search, Model model) {
        List<TransactionDto> transactions;
        if (search != null && !search.isBlank()) {
            transactions = transactionService.searchTransactionsByUser(search);
        } else {
            transactions = transactionService.getAllTransactions();
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("title", "Transaction List");
        return "transactions/list";
    }

    // Show form to create new transaction
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("transactionDto", new TransactionDto());
        model.addAttribute("title", "New Transaction");
        return "transactions/form";
    }

    // Handle form submission to create new transaction
    @PostMapping
    public String createTransaction(@ModelAttribute TransactionDto transactionDto, Model model) {
        try {
            transactionService.createTransaction(transactionDto);
            return "redirect:/transactions";
        } catch (RuntimeException ex) {
            model.addAttribute("transactionDto", transactionDto);
            model.addAttribute("title", "New Transaction");
            model.addAttribute("errorMessage", ex.getMessage());
            return "transactions/form";
        }
    }

    // Show form to edit existing transaction
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TransactionDto dto = transactionService.getTransactionById(id);
        model.addAttribute("transactionDto", dto);
        model.addAttribute("title", "Edit Transaction");
        return "transactions/form";
    }

    // Handle form submission to update transaction
    @PostMapping("/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute TransactionDto transactionDto, Model model) {
        try {
            transactionService.updateTransaction(id, transactionDto);
            return "redirect:/transactions";
        } catch (RuntimeException ex) {
            model.addAttribute("transactionDto", transactionDto);
            model.addAttribute("title", "Edit Transaction");
            model.addAttribute("errorMessage", ex.getMessage());
            return "transactions/form";
        }
    }

    // Delete transaction
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }

    // Return a borrowed book (mark transaction returned)
    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        transactionService.returnBook(id);
        return "redirect:/transactions";
    }

    // List transactions for a specific user
    @GetMapping("/user/{userId}")
    public String listTransactionsByUser(@PathVariable Long userId, Model model) {
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);
        model.addAttribute("transactions", transactions);
        model.addAttribute("title", "User Transactions");
        return "transactions/list";
    }

    // List transactions for a specific book
    @GetMapping("/book/{bookId}")
    public String listTransactionsByBook(@PathVariable Long bookId, Model model) {
        List<TransactionDto> transactions = transactionService.getTransactionsByBookId(bookId);
        model.addAttribute("transactions", transactions);
        model.addAttribute("title", "Book Transactions");
        return "transactions/list";
    }

    @GetMapping("/search/books")
    @ResponseBody
    public List<Map<String, Object>> searchBooks(@RequestParam("query") String query) {
        return transactionService.searchBookTitles(query);
    }

    @GetMapping("/search/users")
    @ResponseBody
    public List<Map<String, Object>> searchUsers(@RequestParam("query") String query) {
        return transactionService.searchUserNames(query);
    }


}
