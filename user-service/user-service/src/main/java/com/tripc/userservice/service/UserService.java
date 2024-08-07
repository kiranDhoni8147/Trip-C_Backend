package com.tripc.userservice.service;

import com.tripc.userservice.dto.LoginRequestDto;
import com.tripc.userservice.dto.UserDto;
import com.tripc.userservice.exception.InvalidOrExpiredOtpException;
import com.tripc.userservice.exception.LoginException;
import com.tripc.userservice.model.User;

public interface UserService {

    void register(UserDto userDto);

    String login(LoginRequestDto loginRequestDto) throws LoginException;

    User getUserByPhoneNumber(String phoneNumber);


    void forgotPassword(String phoneNumber);

    void resetPassword(String otp,String newPassword) throws InvalidOrExpiredOtpException;
}
