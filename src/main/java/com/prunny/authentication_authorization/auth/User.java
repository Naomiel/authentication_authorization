package com.prunny.authentication_authorization.auth;


import com.accelerex.tasks_manager.model.auth.enums.Role;
import com.accelerex.tasks_manager.model.auth.enums.SecurityQuestion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @JsonIgnore
    private String password;
    private String otp;
    private boolean isDisabled;
    private boolean isAccountVerified;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date modifiedAt;
    @Enumerated(EnumType.STRING)
    private SecurityQuestion securityQuestion;
    private String securityAnswer;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Transient
    private String fullName;

}
