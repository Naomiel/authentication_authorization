package com.prunny.authentication_authorization.auth;

import com.accelerex.tasks_manager.model.auth.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    private String token;
    private LocalDateTime expiryTime;
    private TokenType tokenType;
}
