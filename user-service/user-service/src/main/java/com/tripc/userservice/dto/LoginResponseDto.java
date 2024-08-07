package com.tripc.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String status;
    private String message;
    private String token;

    public LoginResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
