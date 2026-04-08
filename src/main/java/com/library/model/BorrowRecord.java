package com.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false) private LocalDate borrowDate;
    @Column(nullable = false) private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

    private Double fineAmount;
    private Boolean finePaid;

    public enum BorrowStatus { BORROWED, RETURNED, OVERDUE }

    @PrePersist
    public void prePersist() {
        if (status == null) status = BorrowStatus.BORROWED;
        if (finePaid == null) finePaid = false;
        if (fineAmount == null) fineAmount = 0.0;
    }
}