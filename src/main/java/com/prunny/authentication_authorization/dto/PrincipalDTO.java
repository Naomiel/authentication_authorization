package com.prunny.authentication_authorization.dto;

public record PrincipalDTO(String message, boolean isSuccessful, String token) {
}
