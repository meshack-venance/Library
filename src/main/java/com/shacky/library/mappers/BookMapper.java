package com.shacky.library.mappers;

import com.shacky.library.dtos.BookDto;
import com.shacky.library.entities.Book;

public final class BookMapper {

    private BookMapper() {
    }

    public static BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .subject(book.getSubject())
                .author(book.getAuthor())
                .gradeLevel(book.getGradeLevel())
                .publicationYear(book.getPublicationYear())
                .price(book.getPrice())
                .bookNumber(book.getBookNumber())
                .category(book.getCategory())
                .build();
    }

    public static Book toEntity(BookDto dto) {
        if (dto == null) {
            return null;
        }

        return Book.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .subject(dto.getSubject())
                .author(dto.getAuthor())
                .gradeLevel(dto.getGradeLevel())
                .publicationYear(dto.getPublicationYear())
                .price(dto.getPrice())
                .bookNumber(dto.getBookNumber())
                .category(dto.getCategory())
                .build();
    }
}
