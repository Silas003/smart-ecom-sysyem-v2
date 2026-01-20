package com.amalitech.demo.models;
//import com.amalitech.demo.utils.UniqueEmail;
//import com.amalitech.demo.utils.UniqueUserName;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name="users")
public class User {

    public User(){};

    public User(String username, String email, String password, String userRole){
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @UniqueUserName
    @Size(min=5, message="Username must be at least 5 characters long")
    @NotBlank(message="Username cannot be blank")
    private String username;

//    @UniqueEmail
    @Email
    @NotBlank
    private String email;

    @NotBlank(message="Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message="User role cannot be blank")
    private String userRole;

}
