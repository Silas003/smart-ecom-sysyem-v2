package com.amalitech.demo.models;

import com.amalitech.demo.dto.CartStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

@Enumerated(EnumType.STRING)
private CartStatus status;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Cart(User user) {
        this.user = user;
        this.status = CartStatus.active;
    }

    @PrePersist
    protected void onCreate() {
        this.status = CartStatus.active;
    }


    public Cart(){};
}