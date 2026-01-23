package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @NotNull
    private Product product;

    @NotNull
    @Column(name = "quantity_in_stock")
    @PositiveOrZero
    private int stockQuantity;

    @NotNull
    @Column(name = "quantity_in_reserved")
    @PositiveOrZero
    private int reservedQuantity;

    @NotBlank
    @NotNull
    @Column(name="stock_status")
    private String stockStatus;

    @Column(name = "created_at" , columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at" , columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @NotNull
    private LocalDateTime updatedAt;

    public Inventory(){}
    public Inventory(Product product, int stockQuantity, int reservedQuantity, String stockStatus){
        this.product = product;
        this.stockQuantity = stockQuantity;
        this.reservedQuantity = reservedQuantity;
        this.stockStatus = stockStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
