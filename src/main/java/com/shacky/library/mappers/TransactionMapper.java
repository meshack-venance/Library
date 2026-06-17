package com.shacky.library.mappers;

import com.shacky.library.dtos.TransactionDto;
import com.shacky.library.entities.Book;
import com.shacky.library.entities.Transaction;
import com.shacky.library.entities.User;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        Book book = transaction.getBook();
        User user = transaction.getUser();

        return TransactionDto.builder()
                .id(transaction.getId())
                .bookId(book.getId())
                .bookAuthor(book.getAuthor())
                .bookTitle(book.getTitle())
                .bookSubject(book.getSubject())
                .bookNumber(book.getBookNumber())
                .bookGradeLevel(book.getGradeLevel())
                .userId(user.getId())
                .userType(user.getUserType())
                .userFirstName(user.getFirstName())
                .userMiddleName(user.getMiddleName())
                .userLastName(user.getLastName())
                .userEmail(user.getEmail())
                .status(transaction.getStatus())
                .borrowDate(transaction.getBorrowDate())
                .returnDate(transaction.getReturnDate())
                .build();
    }

    public static Transaction toEntity(TransactionDto dto, Book book, User user) {
        if (dto == null) {
            return null;
        }

        return Transaction.builder()
                .book(book)
                .user(user)
                .status(dto.getStatus())
                .borrowDate(dto.getBorrowDate())
                .returnDate(dto.getReturnDate())
                .build();
    }
}
