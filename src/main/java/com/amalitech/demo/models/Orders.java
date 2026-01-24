package com.amalitech.demo.models;


import com.amalitech.demo.dto.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @NotNull
    private LocalDateTime orderDate;

    @PositiveOrZero(message = "order total amount cannot be less than zero.")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Orders(User user, Double totalAmount, OrderStatus status) {
        this.user = user;
        this.totalAmount =totalAmount;
        this.status =status;
    }

    @PrePersist
    private void onCreate(){
        this.orderDate = LocalDateTime.now();
    }

    @PreUpdate void onUpdate(){
        this.orderDate = LocalDateTime.now();
    }

    public Orders(){}

}
