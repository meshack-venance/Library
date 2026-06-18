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

    private static final String TRANSACTION_FORM_VIEW = "transactions/form";
    private static final String TRANSACTION_LIST_VIEW = "transactions/list";
    private static final String REDIRECT_TRANSACTIONS = "redirect:/transactions";

    private final TransactionService transactionService;

    @GetMapping
    public String listTransactions(@RequestParam(required = false) String search, Model model) {
        addTransactionListAttributes(model, findTransactions(search), "Transaction List");
        return TRANSACTION_LIST_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        addTransactionFormAttributes(model, new TransactionDto(), "New Transaction", null);
        return TRANSACTION_FORM_VIEW;
    }

    @PostMapping
    public String createTransaction(@ModelAttribute TransactionDto transactionDto, Model model) {
        try {
            transactionService.createTransaction(transactionDto);
            return REDIRECT_TRANSACTIONS;
        } catch (RuntimeException ex) {
            addTransactionFormAttributes(model, transactionDto, "New Transaction", ex.getMessage());
            return TRANSACTION_FORM_VIEW;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        addTransactionFormAttributes(model, transactionService.getTransactionById(id), "Edit Transaction", null);
        return TRANSACTION_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute TransactionDto transactionDto, Model model) {
        try {
            transactionService.updateTransaction(id, transactionDto);
            return REDIRECT_TRANSACTIONS;
        } catch (RuntimeException ex) {
            addTransactionFormAttributes(model, transactionDto, "Edit Transaction", ex.getMessage());
            return TRANSACTION_FORM_VIEW;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return REDIRECT_TRANSACTIONS;
    }

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        transactionService.returnBook(id);
        return REDIRECT_TRANSACTIONS;
    }

    @GetMapping("/user/{userId}")
    public String listTransactionsByUser(@PathVariable Long userId, Model model) {
        addTransactionListAttributes(model, transactionService.getTransactionsByUserId(userId), "User Transactions");
        return TRANSACTION_LIST_VIEW;
    }

    @GetMapping("/book/{bookId}")
    public String listTransactionsByBook(@PathVariable Long bookId, Model model) {
        addTransactionListAttributes(model, transactionService.getTransactionsByBookId(bookId), "Book Transactions");
        return TRANSACTION_LIST_VIEW;
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

    private List<TransactionDto> findTransactions(String search) {
        if (search != null && !search.isBlank()) {
            return transactionService.searchTransactionsByUser(search);
        }
        return transactionService.getAllTransactions();
    }

    private void addTransactionListAttributes(Model model, List<TransactionDto> transactions, String title) {
        model.addAttribute("transactions", transactions);
        model.addAttribute("title", title);
    }

    private void addTransactionFormAttributes(Model model, TransactionDto transactionDto, String title, String errorMessage) {
        model.addAttribute("transactionDto", transactionDto);
        model.addAttribute("title", title);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
    }

}
