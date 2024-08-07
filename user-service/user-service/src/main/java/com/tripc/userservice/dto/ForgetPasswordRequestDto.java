package com.tripc.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgetPasswordRequestDto {

    @NotBlank
    private String phoneNumber;
}
