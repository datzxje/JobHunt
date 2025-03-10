package com.jobhunt.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobhunt.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@EntityListeners(AuditTrailListener.class)
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private String password;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT + 7")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT + 7")
    private Instant updatedAt;

}
