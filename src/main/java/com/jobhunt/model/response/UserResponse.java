package com.jobhunt.model.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class UserResponse {

    private Long id;

    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private Instant createdAt;

    private Instant updatedAt;

    private String status;

    private List<String> roles;

}
