package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @Column(name = "status",columnDefinition = "VARCHAR(20) DEFAULT 'active' CHECK(status in ('active','checkedout','cancelled'))")
    private String status;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Cart(User user, String active) {
        this.user = user;
        this.status = active;
    }

    @PrePersist
    protected void onCreate() {
        this.status = "active";
    }


    public Cart(){};
}