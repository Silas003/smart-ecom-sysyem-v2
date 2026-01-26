package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products",uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_product_name_constraint",
                columnNames = {"name"}
        )
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,name = "id")
    private Long id;

    @NotBlank
    @NotNull
    @Column(columnDefinition = "TEXT UNIQUE", nullable = false)
    private String name;

    @PositiveOrZero
    @Min(0)
    @Column(columnDefinition = "DECIMAL(12,2) DEFAULT 0.0", nullable = false)
    private Double price;

    @Column(name = "created_at" , columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at" , columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @NotNull
    private LocalDateTime updatedAt;

    @Column(name = "stock_quantity", columnDefinition = "INT DEFAULT 0", nullable = false)
    @PositiveOrZero
    private Integer stockQuantity;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Product(){}
    public Product(String name, Double price, Integer stockQuantity, Category category){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
}
