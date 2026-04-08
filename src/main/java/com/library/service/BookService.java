package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepo;

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn)
            .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + isbn));
    }

    public List<Book> searchBooks(String query) {
        return bookRepo.search(query);
    }

    public List<Book> getAvailableBooks() {
        return bookRepo.findByAvailableCopiesGreaterThan(0);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepo.findByGenreIgnoreCase(genre);
    }

    @Transactional
    public Book addBook(Book book) {
        if (bookRepo.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        // Available copies default to total if not set
        if (book.getAvailableCopies() == 0) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        return bookRepo.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updated) {
        Book existing = getBookById(id);
        int borrowedCopies = existing.getTotalCopies() - existing.getAvailableCopies();

        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setGenre(updated.getGenre());
        existing.setPublisher(updated.getPublisher());
        existing.setPublishedYear(updated.getPublishedYear());
        existing.setTotalCopies(updated.getTotalCopies());
        // Recalculate available = new total - currently borrowed
        int newAvailable = updated.getTotalCopies() - borrowedCopies;
        existing.setAvailableCopies(Math.max(0, newAvailable));
        return bookRepo.save(existing);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new RuntimeException("Cannot delete book — some copies are currently borrowed");
        }
        bookRepo.deleteById(id);
    }

    // Called by BorrowService
    @Transactional
    public void decrementAvailable(Long bookId) {
        Book book = getBookById(bookId);
        if (book.getAvailableCopies() <= 0)
            throw new RuntimeException("No available copies of: " + book.getTitle());
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);
    }

    @Transactional
    public void incrementAvailable(Long bookId) {
        Book book = getBookById(bookId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.save(book);
    }

    // Inventory summary
    public record InventorySummary(long totalBooks, long totalCopies, long availableCopies, long borrowedCopies) {}

    public InventorySummary getInventorySummary() {
        List<Book> all = bookRepo.findAll();
        long total   = all.size();
        long copies  = all.stream().mapToLong(Book::getTotalCopies).sum();
        long avail   = all.stream().mapToLong(Book::getAvailableCopies).sum();
        return new InventorySummary(total, copies, avail, copies - avail);
    }
}