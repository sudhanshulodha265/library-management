package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String address;

    @Column(nullable = false)
    private LocalDate membershipDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    public enum MemberStatus { ACTIVE, SUSPENDED, EXPIRED }

    @PrePersist
    public void prePersist() {
        if (membershipDate == null) membershipDate = LocalDate.now();
        if (status == null) status = MemberStatus.ACTIVE;
    }
}