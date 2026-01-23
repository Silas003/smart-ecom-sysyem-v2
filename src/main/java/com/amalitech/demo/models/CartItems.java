package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
//@Table(name = "cart_item")
public class CartItems{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;

    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @PositiveOrZero(message = "Unit price must be zero or positive")
    @Column(name = "unit_price",nullable = false)
    private Double unitPrice;

    @PositiveOrZero(message = "Total price must be zero or positive")
    @Column(name = "total_price",nullable = false)
    private Double totalPrice;

    public CartItems(){};

    @PrePersist
    public void onCreate(){
        this.updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
