package com.amalitech.demo.models;

import com.amalitech.demo.dto.Provider;
import com.amalitech.demo.dto.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name="users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    public User(){};

    public User(String username, String email, String password, UserRole userRole){
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=5, message="Username must be at least 5 characters long")
    @NotBlank(message="Username cannot be blank")
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank(message="Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Provider provider;

}
