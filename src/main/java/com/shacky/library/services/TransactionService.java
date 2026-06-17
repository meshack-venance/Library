package com.shacky.library.services;

import com.shacky.library.dtos.TransactionDto;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    TransactionDto createTransaction(TransactionDto transactionDto);

    TransactionDto updateTransaction(Long id, TransactionDto transactionDto);

    TransactionDto getTransactionById(Long id);

    List<TransactionDto> searchTransactionsByUser(String search);

    List<TransactionDto> getAllTransactions();

    void deleteTransaction(Long id);

    long countBorrowedBooks();

    List<TransactionDto> getTransactionsByUserId(Long userId);

    List<TransactionDto> getTransactionsByBookId(Long bookId);

    TransactionDto returnBook(Long transactionId);

    List<Map<String, Object>> searchBookTitles(String query);

    List<Map<String, Object>> searchUserNames(String query);

}
