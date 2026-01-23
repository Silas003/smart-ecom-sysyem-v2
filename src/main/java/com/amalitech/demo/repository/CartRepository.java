package com.amalitech.demo.repository;

import com.amalitech.demo.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("""
   SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
   FROM Cart c 
   WHERE c.user.id = :userId AND c.status = :status
    """)
    boolean existsByUserIdAndStatus(Long userId, String status);


    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId and c.status = 'active'")
    Cart findByUserIdAndStatus(Long userId, String status);
}
