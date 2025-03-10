package com.jobhunt.model.response;

import lombok.Data;

@Data
public class SignUpResponse {

    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private String status;

}
