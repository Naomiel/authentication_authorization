package com.prunny.authentication_authorization.eventListeners;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class OnUserLogoutSuccessListener extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String email;
    private final String token;
    private final Date eventTime;

    public OnUserLogoutSuccessListener(String email, String token) {
        super(email);
        this.email = email;
        this.token = token;
        this.eventTime = Date.from(Instant.now());
    }
}
