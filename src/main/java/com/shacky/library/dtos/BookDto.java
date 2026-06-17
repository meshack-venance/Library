package com.shacky.library.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String subject;
    private String gradeLevel;
    private String author;
    private int publicationYear;
    private String category;
    private double price;
    private String bookNumber;
    private Long borrowCount;

    public BookDto(Long id, String title, String subject, String gradeLevel, String author, int publicationYear,
                   String category, double price, Long borrowCount) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.author = author;
        this.publicationYear = publicationYear;
        this.category = category;
        this.price = price;
        this.borrowCount = borrowCount;
    }

    public BookDto(Long id, String title, String subject, String gradeLevel, String author, int publicationYear,
                   String category, double price, String bookNumber, Long borrowCount) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.author = author;
        this.publicationYear = publicationYear;
        this.category = category;
        this.price = price;
        this.bookNumber = bookNumber;
        this.borrowCount = borrowCount;
    }
}
