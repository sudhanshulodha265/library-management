package com.library.controller;

import com.library.model.BorrowRecord;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // GET /api/borrow/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<BorrowService.DashboardSummary> dashboard() {
        return ResponseEntity.ok(borrowService.getDashboard());
    }

    // GET /api/borrow
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAll() {
        return ResponseEntity.ok(borrowService.getAllRecords());
    }

    // GET /api/borrow/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecord> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.getBorrowRecord(id));
    }

    // GET /api/borrow/overdue
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> overdue() {
        return ResponseEntity.ok(borrowService.getOverdueRecords());
    }

    // GET /api/borrow/member/{memberId}
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowRecord>> memberHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getMemberHistory(memberId));
    }

    // GET /api/borrow/book/{bookId}
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BorrowRecord>> bookHistory(@PathVariable Long bookId) {
        return ResponseEntity.ok(borrowService.getBookHistory(bookId));
    }

    // GET /api/borrow/fines/{memberId}
    @GetMapping("/fines/{memberId}")
    public ResponseEntity<List<BorrowRecord>> unpaidFines(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowService.getUnpaidFines(memberId));
    }

    // POST /api/borrow  body: { "memberId": 1, "bookId": 2 }
    @PostMapping
    public ResponseEntity<BorrowRecord> borrowBook(@RequestBody Map<String, Long> body) {
        Long memberId = body.get("memberId");
        Long bookId   = body.get("bookId");
        if (memberId == null || bookId == null) {
            throw new RuntimeException("Request must include memberId and bookId");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(borrowService.borrowBook(memberId, bookId));
    }

    // PUT /api/borrow/{id}/return
    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.returnBook(id));
    }

    // PUT /api/borrow/{id}/pay-fine
    @PutMapping("/{id}/pay-fine")
    public ResponseEntity<BorrowRecord> payFine(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.payFine(id));
    }

    // POST /api/borrow/update-overdue  (run to refresh overdue status + fines)
    @PostMapping("/update-overdue")
    public ResponseEntity<Map<String, Object>> updateOverdue() {
        int count = borrowService.markOverdueRecords();
        return ResponseEntity.ok(Map.of("message", "Overdue records updated", "count", count));
    }
}