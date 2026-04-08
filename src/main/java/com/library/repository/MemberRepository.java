package com.library.repository;

import com.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByStatus(Member.MemberStatus status);

    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Member> search(@Param("q") String query);
}