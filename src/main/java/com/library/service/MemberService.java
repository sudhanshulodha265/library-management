package com.library.service;

import com.library.model.Member;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepo;
    private final BorrowRecordRepository borrowRepo;

    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public List<Member> searchMembers(String query) {
        return memberRepo.search(query);
    }

    public List<Member> getMembersByStatus(Member.MemberStatus status) {
        return memberRepo.findByStatus(status);
    }

    @Transactional
    public Member registerMember(Member member) {
        if (memberRepo.findByEmail(member.getEmail()).isPresent()) {
            throw new RuntimeException("Member with email " + member.getEmail() + " already exists");
        }
        return memberRepo.save(member);
    }

    @Transactional
    public Member updateMember(Long id, Member updated) {
        Member existing = getMemberById(id);
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        return memberRepo.save(existing);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member m = getMemberById(id);
        long activeBorrows = borrowRepo.countByMemberIdAndStatus(id,
            com.library.model.BorrowRecord.BorrowStatus.BORROWED);
        if (activeBorrows > 0) {
            throw new RuntimeException("Cannot delete member — they have " + activeBorrows + " book(s) still borrowed");
        }
        memberRepo.deleteById(id);
    }

    @Transactional
    public Member updateStatus(Long id, Member.MemberStatus status) {
        Member m = getMemberById(id);
        m.setStatus(status);
        return memberRepo.save(m);
    }

    // Fine summary
    public record MemberFineSummary(Long memberId, String memberName, Double totalUnpaidFine) {}

    public MemberFineSummary getMemberFineSummary(Long memberId) {
        Member m = getMemberById(memberId);
        Double unpaid = borrowRepo.totalUnpaidFine(memberId);
        return new MemberFineSummary(memberId, m.getName(), unpaid != null ? unpaid : 0.0);
    }
}