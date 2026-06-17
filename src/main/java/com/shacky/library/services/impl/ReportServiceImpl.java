package com.shacky.library.services.impl;

import com.shacky.library.dtos.BookDto;
import com.shacky.library.dtos.TransactionDto;
import com.shacky.library.dtos.UserDto;
import com.shacky.library.entities.Transaction;
import com.shacky.library.entities.User;
import com.shacky.library.repositories.TransactionRepository;
import com.shacky.library.repositories.UserRepository;
import com.shacky.library.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookDto> getTopBorrowedBooks() {
        Map<String, Long> borrowCountMap = transactionRepository.findAll().stream()
                .filter(tx -> tx.getBook() != null &&
                        (tx.getStatus().equalsIgnoreCase("BORROWED") || tx.getStatus().equalsIgnoreCase("RETURNED")))
                .collect(Collectors.groupingBy(
                        tx -> tx.getBook().getTitle() + "||" + tx.getBook().getGradeLevel(),
                        Collectors.counting()
                ));

        return borrowCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> {
                    String[] parts = entry.getKey().split("\\|\\|");
                    String title = parts[0];
                    String gradeLevel = parts.length > 1 ? parts[1] : "Unknown";

                    return BookDto.builder()
                            .title(title)
                            .gradeLevel(gradeLevel)
                            .borrowCount(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getTopActiveUsers() {
        Map<Long, Long> userBorrowCountMap = transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(t -> t.getUser().getId(), Collectors.counting()));

        return userBorrowCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> {
                    Optional<User> userOpt = userRepository.findById(entry.getKey());
                    return userOpt.map(user -> UserDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .middleName(user.getMiddleName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .userType(user.getUserType())
                            .clsRoom(user.getClsRoom())
                            .totalBorrowed(entry.getValue())
                            .build()
                    ).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getOverdueTransactions() {
        List<Transaction> overdueTx = transactionRepository.findByReturnDateBeforeAndStatusNot(LocalDate.now(), "RETURNED");

        return overdueTx.stream()
                .map(tx -> TransactionDto.builder()
                        .id(tx.getId())
                        .bookId(tx.getBook().getId())
                        .bookTitle(tx.getBook().getTitle())
                        .bookAuthor(tx.getBook().getAuthor())
                        .bookSubject(tx.getBook().getSubject())
                        .userId(tx.getUser().getId())
                        .userFirstName(tx.getUser().getFirstName())
                        .userMiddleName(tx.getUser().getMiddleName())
                        .userLastName(tx.getUser().getLastName())
                        .userEmail(tx.getUser().getEmail())
                        .userType(tx.getUser().getUserType())
                        .status(tx.getStatus())
                        .borrowDate(tx.getBorrowDate())
                        .returnDate(tx.getReturnDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalTransactionCount() {
        return transactionRepository.count();
    }

}
