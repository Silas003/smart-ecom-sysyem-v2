package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItems{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;

    @Column(name = "updated_at" , columnDefinition = "TIMESTAMP NOT NULL DEFAULT NOW()  ")
    private LocalDateTime updatedAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cart_id",columnDefinition = " INT NOT NULL", referencedColumnName = "id")
    private Cart cart;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id",columnDefinition = " INT NOT NULL", referencedColumnName = "id")
    private Product product;

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
