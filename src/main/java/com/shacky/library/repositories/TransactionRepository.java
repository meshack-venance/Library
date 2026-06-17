package com.shacky.library.repositories;

import com.shacky.library.dtos.UserDto;
import com.shacky.library.entities.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    long countByStatus(String status);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByBookId(Long bookId);

    List<Transaction> findByReturnDateBeforeAndStatusNot(LocalDate date, String status);

    List<Transaction> findByStatus(String status);


    @Query("SELECT new com.shacky.library.dtos.UserDto(u.id, u.firstName, u.lastName, u.email, COUNT(t), u.userType, u.clsRoom) " +
            "FROM Transaction t JOIN t.user u " +
            "WHERE t.status = 'borrowed' OR t.status = 'returned' " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(t) DESC")
    List<UserDto> findTopActiveUsers(Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAllTransactions();

    @Query("SELECT t FROM Transaction t WHERE " +
            "LOWER(t.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.user.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Transaction> searchByUserName(@Param("search") String search);
}
