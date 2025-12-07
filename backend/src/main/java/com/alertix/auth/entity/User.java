package com.alertix.auth.entity;

import com.alertix.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * User Entity
 *
 * Represents a user account in the system
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.READER;

    @Builder.Default
    private Boolean enabled = true;

    public enum UserRole {
        ADMIN, OPERATOR, READER
    }
}
