package com.shacky.library.dtos;

import com.shacky.library.common.util.NameFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Long bookId;
    private String bookAuthor;
    private String bookTitle;
    private String bookSubject;
    private String bookNumber;
    private String bookGradeLevel;
    private Long userId;
    private String userType;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String userEmail;
    private String status;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public String getFullName() {
        return NameFormatter.fullName(userFirstName, userMiddleName, userLastName);
    }
}
