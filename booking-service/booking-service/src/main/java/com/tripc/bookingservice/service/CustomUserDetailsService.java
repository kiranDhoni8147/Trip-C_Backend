package com.tripc.bookingservice.service;

import com.tripc.bookingservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${user.service.url}")  // URL of your UserService
    private String userServiceUrl;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        UserDto userDto=restTemplate.getForObject(userServiceUrl+"/api/user/"+phoneNumber, UserDto.class);
        if(userDto==null){
            throw new UsernameNotFoundException("user not found with phoneNumber "+phoneNumber);
        }
        return new org.springframework.security.core.userdetails.User(
                userDto.getPhoneNumber(),
                userDto.getPassword(),
                new ArrayList<>()
        );
    }
}
