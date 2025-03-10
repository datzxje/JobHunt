package com.jobhunt.model.response;

import lombok.*;

@Data
public class LoginResponse {

    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {

        private Long id;

        private String username;

        private String email;

        private String firstname;

        private String lastname;

    }

}
