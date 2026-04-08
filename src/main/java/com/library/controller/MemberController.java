package com.library.controller;

import com.library.model.Member;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // GET /api/members
    @GetMapping
    public ResponseEntity<List<Member>> getAll() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    // GET /api/members/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    // GET /api/members/search?q=ravi
    @GetMapping("/search")
    public ResponseEntity<List<Member>> search(@RequestParam String q) {
        return ResponseEntity.ok(memberService.searchMembers(q));
    }

    // GET /api/members/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Member>> byStatus(@PathVariable Member.MemberStatus status) {
        return ResponseEntity.ok(memberService.getMembersByStatus(status));
    }

    // GET /api/members/{id}/fines
    @GetMapping("/{id}/fines")
    public ResponseEntity<MemberService.MemberFineSummary> fines(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberFineSummary(id));
    }

    // POST /api/members
    @PostMapping
    public ResponseEntity<Member> register(@Valid @RequestBody Member member) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.registerMember(member));
    }

    // PUT /api/members/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @RequestBody Member member) {
        return ResponseEntity.ok(memberService.updateMember(id, member));
    }

    // PATCH /api/members/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Member> updateStatus(
            @PathVariable Long id,
            @RequestParam Member.MemberStatus status) {
        return ResponseEntity.ok(memberService.updateStatus(id, status));
    }

    // DELETE /api/members/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(Map.of("message", "Member deleted successfully"));
    }
}