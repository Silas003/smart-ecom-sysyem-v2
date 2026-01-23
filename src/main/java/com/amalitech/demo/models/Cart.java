package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name ="carts")
public class Cart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @Column(name = "status",columnDefinition = "VARCHAR(20) NOT NULL CHECK(status in ('active','checkedout','cancelled'))DEFAULT 'active'")
    private String status;

    @NotBlank
    @Column(name = "update_at", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        this.status = "active";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public Cart(){};
}