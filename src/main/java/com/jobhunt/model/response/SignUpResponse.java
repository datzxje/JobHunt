package com.jobhunt.model.response;

import lombok.Data;

@Data
public class SignUpResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String keycloakUsername;

    private String email;

    private String phoneNumber;

    private String profilePictureUrl;

    private String status;

}
