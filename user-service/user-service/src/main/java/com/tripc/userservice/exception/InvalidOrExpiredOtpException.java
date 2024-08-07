package com.tripc.userservice.exception;



public class InvalidOrExpiredOtpException extends Exception{

    public InvalidOrExpiredOtpException(String invalidOrExpiredOtp) {
        super(invalidOrExpiredOtp);
    }
}
