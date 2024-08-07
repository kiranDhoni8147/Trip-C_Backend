package com.tripc.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "Name is mandatory")
    private String userName;

    @Email(message = "Enter the proper Email Format")
    @NotBlank(message = "Email Field Cannot be Empty")
    private String email;

    @NotBlank(message = "password is Mandatory")
    @Size(min = 8,message = "Password should be at least 8 characters long")
    private String password;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid and between 10 to 15 digits")
    private String phoneNumber;

}
