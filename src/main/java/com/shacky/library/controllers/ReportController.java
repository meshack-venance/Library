package com.shacky.library.controllers;

import com.shacky.library.dtos.BookDto;
import com.shacky.library.dtos.TransactionDto;
import com.shacky.library.dtos.UserDto;
import com.shacky.library.services.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    @GetMapping
    public String showReports(Model model) {
        List<BookDto> topBorrowedBooks = reportService.getTopBorrowedBooks();
        List<UserDto> activeUsers = reportService.getTopActiveUsers();
        List<TransactionDto> overdueTransactions = reportService.getOverdueTransactions();
        long totalTransactions = reportService.getTotalTransactionCount();

        model.addAttribute("topBooks", topBorrowedBooks);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("overdues", overdueTransactions);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("title", "Library Reports");

        return "reports/dashboard";
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdfReport() {
        try {
            List<BookDto> topBorrowedBooks = reportService.getTopBorrowedBooks();
            List<UserDto> activeUsers = reportService.getTopActiveUsers();
            List<TransactionDto> overdueTransactions = reportService.getOverdueTransactions();
            long totalTransactions = reportService.getTotalTransactionCount();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 54, 72);
            PdfWriter.getInstance(document, out);
            document.open();

            BaseColor primaryColor = new BaseColor(13, 110, 253);
            BaseColor successColor = new BaseColor(25, 135, 84);
            BaseColor dangerColor = new BaseColor(220, 53, 69);
            BaseColor lightGray = new BaseColor(240, 240, 240);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, primaryColor);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.GRAY);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, primaryColor);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Font smallGrayFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);

            Paragraph title = new Paragraph("Library Reports", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph dateParagraph = new Paragraph("Date: " + LocalDate.now().toString(), smallGrayFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.setSpacingAfter(10);
            document.add(dateParagraph);

            Paragraph subtitle = new Paragraph("Real-time overview of library activities", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            PdfPTable summaryTable = new PdfPTable(1);
            summaryTable.setWidthPercentage(40);
            summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell summaryCell = new PdfPCell();
            summaryCell.setBackgroundColor(primaryColor);
            summaryCell.setPadding(20);
            summaryCell.setBorder(Rectangle.NO_BORDER);

            Paragraph summaryTitle = new Paragraph("Total Transactions", tableHeaderFont);
            summaryTitle.setAlignment(Element.ALIGN_CENTER);
            summaryTitle.setSpacingAfter(10);
            summaryCell.addElement(summaryTitle);

            Paragraph summaryCount = new Paragraph(String.valueOf(totalTransactions),
                    new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD, BaseColor.WHITE));
            summaryCount.setAlignment(Element.ALIGN_CENTER);
            summaryCell.addElement(summaryCount);

            summaryTable.addCell(summaryCell);
            summaryTable.setSpacingAfter(25);
            document.add(summaryTable);

            document.add(new Paragraph("Top Borrowed Books by Title & Grade", sectionTitleFont));
            document.add(new Paragraph(" "));
            PdfPTable bookTable = new PdfPTable(new float[]{1, 5, 2, 2});
            bookTable.setWidthPercentage(100);
            addColoredTableHeader(bookTable, tableHeaderFont, primaryColor, "No.", "Title", "Grade", "Borrow Count");

            int count = 1;
            for (BookDto book : topBorrowedBooks) {
                addTableRow(bookTable, tableBodyFont, count++,
                        book.getTitle(),
                        book.getGradeLevel(),
                        String.valueOf(book.getBorrowCount() != null ? book.getBorrowCount() : 0),
                        lightGray);
            }
            document.add(bookTable);

            Paragraph note = new Paragraph(
                    "Note: Borrow count is grouped by book title and grade level to reflect usage across all available copies.",
                    smallGrayFont
            );
            note.setSpacingBefore(5);
            note.setAlignment(Element.ALIGN_LEFT);
            document.add(note);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Top Active Users", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, successColor)));
            document.add(new Paragraph(" "));
            PdfPTable userTable = new PdfPTable(new float[]{1, 5, 6, 2});
            userTable.setWidthPercentage(100);
            addColoredTableHeader(userTable, tableHeaderFont, successColor, "No.", "Name", "Email", "Books Borrowed");

            count = 1;
            for (UserDto user : activeUsers) {
                addTableRow(userTable, tableBodyFont, count++, user.getFullName(), user.getEmail(),
                        String.valueOf(user.getTotalBorrowed() != null ? user.getTotalBorrowed() : 0), lightGray);
            }
            document.add(userTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Overdue Transactions", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, dangerColor)));
            document.add(new Paragraph(" "));
            PdfPTable txTable = new PdfPTable(new float[]{1, 5, 5, 3, 3});
            txTable.setWidthPercentage(100);
            addColoredTableHeader(txTable, tableHeaderFont, dangerColor, "No.", "Book Title", "User", "Borrow Date", "Due Date");

            count = 1;
            for (TransactionDto tx : overdueTransactions) {
                addTableRow(txTable, tableBodyFont, count++,
                        tx.getBookTitle(),
                        tx.getFullName(),
                        tx.getBorrowDate() != null ? tx.getBorrowDate().toString() : "",
                        tx.getReturnDate() != null ? tx.getReturnDate().toString() : "",
                        lightGray);
            }
            document.add(txTable);
            document.add(Chunk.NEWLINE);

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(80);
            signatureTable.setSpacingBefore(50);
            signatureTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell preparedByCell = new PdfPCell();
            preparedByCell.setBorder(Rectangle.NO_BORDER);
            preparedByCell.addElement(new Paragraph("Prepared By:", subtitleFont));
            preparedByCell.addElement(new Paragraph("Library Management System", subtitleFont));
            preparedByCell.addElement(new Paragraph("Name", smallGrayFont));

            PdfPCell approvedByCell = new PdfPCell();
            approvedByCell.setBorder(Rectangle.NO_BORDER);
            approvedByCell.addElement(new Paragraph("Approved By:", subtitleFont));
            approvedByCell.addElement(new Paragraph("________________________", subtitleFont));
            approvedByCell.addElement(new Paragraph("Name & Signature", smallGrayFont));

            signatureTable.addCell(preparedByCell);
            signatureTable.addCell(approvedByCell);

            document.add(signatureTable);
            document.close();

            byte[] pdfBytes = out.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=library_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Failed to generate library PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addColoredTableHeader(PdfPTable table, Font font, BaseColor bgColor, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(bgColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, Font font, int number, String col1, String col2, String col3, BaseColor bgColor) {
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(String.valueOf(number), font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col1, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col2, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col3, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, Font font, int number, String col1, String col2, String col3, String col4, BaseColor bgColor) {
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(String.valueOf(number), font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col1, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col2, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col3, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(col4, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
