package com.prunny.authentication_authorization.service;

public interface EmailService {
    void sendEmail(String email, String subject, String body);

}
