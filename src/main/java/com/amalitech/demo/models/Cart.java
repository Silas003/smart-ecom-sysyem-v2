package com.amalitech.demo.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name ="carts")
public class Cart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Cart(){};
}