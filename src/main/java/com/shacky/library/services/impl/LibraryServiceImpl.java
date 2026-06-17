package com.shacky.library.services.impl;

import com.shacky.library.repositories.BookRepository;
import com.shacky.library.repositories.TransactionRepository;
import com.shacky.library.repositories.UserRepository;
import com.shacky.library.services.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Override
    public long countBooks() {
        return bookRepository.count();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countBorrowedBooks() {
        return transactionRepository.countByStatus("borrowed");
    }
}
