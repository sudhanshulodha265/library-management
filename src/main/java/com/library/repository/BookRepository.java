package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAvailableCopiesGreaterThan(int count);
    List<Book> findByGenreIgnoreCase(String genre);

    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Book> search(@Param("q") String query);
}