package com.prunny.authentication_authorization.auth.enums;

import lombok.Getter;

public enum SecurityQuestion {
    FIRST_PET("What was the name of your first pet?"),
    STREET_GREW_UP("What was name of the street where you grew up?"),
    MOTHER_MAIDEN("What is your mother's maiden name?");

    @Getter
    private String label;

    SecurityQuestion(String label) {
        this.label = label;
    }
}
