package com.amalitech.demo.config;


import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;


@Configuration
public class PreLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepository, OrdersRepository ordersRepository,
                               CategoryRepository categoryRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, ReviewsRepository reviewsRepository) {

        return args -> {

            for (int i = 1; i <= 15; i++) {

                int r = ThreadLocalRandom.current().nextInt(1000, 10000);

                User u = userRepository.save(
                        new User(
                                "AliceOp" + r,
                                String.format("%dw@gmail.com", r),
                                "password1",
                                UserRole.customer
                        )
                );

                Category c = categoryRepository.save(
                        new Category("Category" + r)
                );

                Product p = new Product("Product" + r,
                        Double.valueOf(r),
                        Integer.valueOf(r),
                        c
                );

                Orders o = new Orders(
                        u,Double.valueOf(5353+r), OrderStatus.pending
                );
                ordersRepository.save(o);
                productRepository.save(
                        p
                );

                inventoryRepository.save(
                        new Inventory(p,r,r,"storage location " + r)
                );
                reviewsRepository.save(
                        new Reviews(9, "Great Product " + r,u ,p)
                );
            }
        };
    }
}

