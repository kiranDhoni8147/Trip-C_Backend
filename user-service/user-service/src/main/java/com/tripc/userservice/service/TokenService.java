package com.tripc.userservice.service;

import com.tripc.userservice.model.User;

public interface TokenService {

    public String createToken(User user);
}
