package com.amalitech.demo.repository;

import com.amalitech.demo.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItems,Long> {
}
