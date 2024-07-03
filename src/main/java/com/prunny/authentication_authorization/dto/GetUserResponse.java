package com.prunny.authentication_authorization.dto;

public record GetUserResponse(String firstName, String lastName, String email, String password) {
}
