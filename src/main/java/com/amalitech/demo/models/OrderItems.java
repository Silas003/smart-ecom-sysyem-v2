package com.amalitech.demo.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @PositiveOrZero(message = "quantity cannot be less than zero")
    private Integer quantity;

    @PositiveOrZero
    private Double unitPrice;

    @PositiveOrZero
    private Double totalPrice;

    public OrderItems(){};


}