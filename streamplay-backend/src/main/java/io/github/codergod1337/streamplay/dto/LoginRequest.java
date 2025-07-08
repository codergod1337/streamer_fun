package io.github.codergod1337.streamplay.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
}
