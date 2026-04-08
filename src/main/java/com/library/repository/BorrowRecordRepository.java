package com.library.repository;

import com.library.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByMemberId(Long memberId);
    List<BorrowRecord> findByBookId(Long bookId);
    List<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status);

    Optional<BorrowRecord> findByMemberIdAndBookIdAndStatus(
        Long memberId, Long bookId, BorrowRecord.BorrowStatus status);

    @Query("SELECT r FROM BorrowRecord r WHERE r.status = 'BORROWED' AND r.dueDate < :today")
    List<BorrowRecord> findOverdue(@Param("today") LocalDate today);

    long countByMemberIdAndStatus(Long memberId, BorrowRecord.BorrowStatus status);

    @Query("SELECT r FROM BorrowRecord r WHERE r.member.id = :memberId AND r.finePaid = false AND r.fineAmount > 0")
    List<BorrowRecord> findUnpaidFines(@Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(r.fineAmount), 0) FROM BorrowRecord r WHERE r.member.id = :memberId AND r.finePaid = false")
    Double totalUnpaidFine(@Param("memberId") Long memberId);
}