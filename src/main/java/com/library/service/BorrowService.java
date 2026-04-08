package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.BorrowRecord.BorrowStatus;
import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRepo;
    private final BookService bookService;
    private final MemberService memberService;

    @Value("${library.fine.rate-per-day:5.0}")
    private double fineRatePerDay;

    @Value("${library.loan.duration-days:14}")
    private int loanDurationDays;

    // ── BORROW ──────────────────────────────────────────────
    @Transactional
    public BorrowRecord borrowBook(Long memberId, Long bookId) {
        Member member = memberService.getMemberById(memberId);
        Book   book   = bookService.getBookById(bookId);

        if (member.getStatus() != Member.MemberStatus.ACTIVE) {
            throw new RuntimeException("Member account is " + member.getStatus() + ". Cannot borrow books.");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of \"" + book.getTitle() + "\"");
        }

        // Check if member already has this book borrowed
        borrowRepo.findByMemberIdAndBookIdAndStatus(memberId, bookId, BorrowStatus.BORROWED)
            .ifPresent(r -> { throw new RuntimeException("Member already has this book borrowed (Record #" + r.getId() + ")"); });

        // Check unpaid fines — block if > ₹50
        Double unpaidFine = borrowRepo.totalUnpaidFine(memberId);
        if (unpaidFine != null && unpaidFine > 50.0) {
            throw new RuntimeException(
                String.format("Member has unpaid fines of ₹%.2f. Please pay before borrowing.", unpaidFine));
        }

        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(loanDurationDays);

        BorrowRecord record = BorrowRecord.builder()
            .member(member)
            .book(book)
            .borrowDate(today)
            .dueDate(dueDate)
            .status(BorrowStatus.BORROWED)
            .fineAmount(0.0)
            .finePaid(false)
            .build();

        bookService.decrementAvailable(bookId);
        return borrowRepo.save(record);
    }

    // ── RETURN ──────────────────────────────────────────────
    @Transactional
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = getBorrowRecord(borrowRecordId);

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new RuntimeException("Book has already been returned (Record #" + borrowRecordId + ")");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        double fine = calculateFine(record.getDueDate(), today);
        record.setFineAmount(fine);
        record.setFinePaid(fine == 0.0); // auto mark paid if no fine
        record.setStatus(BorrowStatus.RETURNED);

        bookService.incrementAvailable(record.getBook().getId());
        return borrowRepo.save(record);
    }

    // ── FINE ────────────────────────────────────────────────
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        if (overdueDays <= 0) return 0.0;
        return Math.round(overdueDays * fineRatePerDay * 100.0) / 100.0;
    }

    @Transactional
    public BorrowRecord payFine(Long borrowRecordId) {
        BorrowRecord record = getBorrowRecord(borrowRecordId);
        if (record.getFineAmount() == null || record.getFineAmount() == 0.0) {
            throw new RuntimeException("No fine to pay on record #" + borrowRecordId);
        }
        if (Boolean.TRUE.equals(record.getFinePaid())) {
            throw new RuntimeException("Fine already paid on record #" + borrowRecordId);
        }
        record.setFinePaid(true);
        return borrowRepo.save(record);
    }

    // ── OVERDUE UPDATE ───────────────────────────────────────
    @Transactional
    public int markOverdueRecords() {
        List<BorrowRecord> overdue = borrowRepo.findOverdue(LocalDate.now());
        for (BorrowRecord r : overdue) {
            r.setStatus(BorrowStatus.OVERDUE);
            // Update running fine
            double fine = calculateFine(r.getDueDate(), LocalDate.now());
            r.setFineAmount(fine);
            r.setFinePaid(false);
            borrowRepo.save(r);
        }
        return overdue.size();
    }

    // ── QUERIES ──────────────────────────────────────────────
    public BorrowRecord getBorrowRecord(Long id) {
        return borrowRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Borrow record not found: " + id));
    }

    public List<BorrowRecord> getAllRecords() {
        return borrowRepo.findAll();
    }

    public List<BorrowRecord> getMemberHistory(Long memberId) {
        memberService.getMemberById(memberId); // validate
        return borrowRepo.findByMemberId(memberId);
    }

    public List<BorrowRecord> getBookHistory(Long bookId) {
        bookService.getBookById(bookId); // validate
        return borrowRepo.findByBookId(bookId);
    }

    public List<BorrowRecord> getOverdueRecords() {
        return borrowRepo.findOverdue(LocalDate.now());
    }

    public List<BorrowRecord> getUnpaidFines(Long memberId) {
        memberService.getMemberById(memberId);
        return borrowRepo.findUnpaidFines(memberId);
    }

    public record DashboardSummary(
        long totalBorrowed,
        long currentlyBorrowed,
        long overdueCount,
        double totalFinesOutstanding
    ) {}

    public DashboardSummary getDashboard() {
        long total     = borrowRepo.count();
        long active    = borrowRepo.findByStatus(BorrowStatus.BORROWED).size();
        long overdue   = borrowRepo.findOverdue(LocalDate.now()).size();
        double fines   = borrowRepo.findAll().stream()
            .filter(r -> !Boolean.TRUE.equals(r.getFinePaid()))
            .mapToDouble(r -> r.getFineAmount() != null ? r.getFineAmount() : 0.0)
            .sum();
        return new DashboardSummary(total, active, overdue, Math.round(fines * 100.0) / 100.0);
    }
}