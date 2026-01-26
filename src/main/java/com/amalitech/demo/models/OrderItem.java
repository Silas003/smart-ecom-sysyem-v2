package com.amalitech.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @NotNull
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @NotNull
    private Product product;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double unitPrice;

    @NotNull
    private Double totalPrice;

    public OrderItem() {}
}
